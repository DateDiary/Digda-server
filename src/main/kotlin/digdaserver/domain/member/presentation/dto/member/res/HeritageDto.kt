package digdaserver.domain.member.presentation.dto.member.res

data class HeritageDto(
    val heritageImage: String,
    val heritageText: String
) {
    companion object {
        fun of(heritageImage: String, heritageText: String): HeritageDto {
            return HeritageDto(heritageImage, heritageText)
        }
    }
}
