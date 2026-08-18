package digdaserver.domain.upload.application.service.impl

import digdaserver.domain.upload.application.service.UploadService
import digdaserver.domain.upload.domain.entity.ImagePurpose
import digdaserver.domain.upload.domain.entity.UploadedImage
import digdaserver.domain.upload.domain.repository.UploadedImageRepository
import digdaserver.domain.upload.presentation.dto.res.UploadImageResponse
import digdaserver.domain.user.domain.repository.UserRepository
import digdaserver.global.infra.exception.error.DigdaException
import digdaserver.global.infra.exception.error.ErrorCode
import digdaserver.global.infra.s3.presentation.application.S3Service
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID
import javax.imageio.ImageIO

@Service
@Transactional(readOnly = true)
class UploadServiceImpl(
    private val uploadedImageRepository: UploadedImageRepository,
    private val userRepository: UserRepository,
    private val s3Service: S3Service
) : UploadService {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val MAX_SIZE_BYTES = 100L * 1024 * 1024
        private val ALLOWED_CONTENT_TYPES = setOf("image/png", "image/jpeg", "image/jpg")
    }

    @Transactional
    override fun uploadImage(userId: UUID, file: MultipartFile, purpose: String): UploadImageResponse {
        validateFile(file)

        val imagePurpose = parsePurpose(purpose)

        val user = userRepository.findById(userId)
            .orElseThrow { DigdaException(ErrorCode.USER_NOT_FOUND) }

        val (width, height) = readImageDimensions(file)

        val url = s3Service.storeImage(file, userId.toString())
            ?: throw DigdaException(ErrorCode.SERVER_ERROR)

        val saved = uploadedImageRepository.save(
            UploadedImage(
                user = user,
                url = url,
                width = width,
                height = height,
                purpose = imagePurpose
            )
        )

        return UploadImageResponse.from(saved)
    }

    private fun validateFile(file: MultipartFile) {
        if (file.isEmpty) throw DigdaException(ErrorCode.INVALID_FILE_TYPE)
        if (file.size > MAX_SIZE_BYTES) throw DigdaException(ErrorCode.FILE_TOO_LARGE)

        val contentType = file.contentType?.lowercase()
        if (contentType !in ALLOWED_CONTENT_TYPES) {
            throw DigdaException(ErrorCode.INVALID_FILE_TYPE)
        }
    }

    private fun parsePurpose(purpose: String): ImagePurpose {
        return when (purpose.lowercase()) {
            "profile" -> ImagePurpose.PROFILE
            "group_thumbnail" -> ImagePurpose.GROUP_THUMBNAIL
            "diary" -> ImagePurpose.DIARY
            "quiz" -> ImagePurpose.QUIZ
            "exhibit" -> ImagePurpose.EXHIBIT
            else -> throw DigdaException(ErrorCode.INVALID_PARAMETER)
        }
    }

    /**
     * 이미지 헤더만 읽어 가로·세로를 구한다.
     *
     * 이전에는 ImageIO.read() 로 전체를 디코딩했는데, 이때 잡히는 힙은 파일 크기가 아니라
     * (가로 × 세로 × 4바이트) 다. 4000×3000 사진 한 장이 약 48MB, 8000×6000 이면 약 192MB 라
     * 업로드가 겹치면 OOM 이 나고, 디코딩에 걸리는 시간만큼 응답도 그대로 느려졌다.
     * (크기 정보만 필요한데 픽셀을 전부 푸는 셈이었다.)
     *
     * ImageReader 는 픽셀을 풀지 않고 헤더만 읽으므로 원본 해상도와 무관하게 수 KB / 수 ms 로 끝난다.
     */
    private fun readImageDimensions(file: MultipartFile): Pair<Int, Int> {
        return try {
            file.inputStream.use { stream ->
                ImageIO.createImageInputStream(stream)?.use { input ->
                    val readers = ImageIO.getImageReaders(input)
                    if (!readers.hasNext()) return 0 to 0

                    val reader = readers.next()
                    try {
                        // seekForwardOnly=true, ignoreMetadata=true — 헤더만 보고 끝내기 위한 조합.
                        reader.setInput(input, true, true)
                        reader.getWidth(0) to reader.getHeight(0)
                    } finally {
                        // dispose 를 빼면 리더가 잡고 있는 네이티브 버퍼가 GC 까지 남는다.
                        reader.dispose()
                    }
                } ?: (0 to 0)
            }
        } catch (e: Exception) {
            log.warn("Failed to read image dimensions for file '{}': {}", file.originalFilename, e.message)
            0 to 0
        }
    }
}
