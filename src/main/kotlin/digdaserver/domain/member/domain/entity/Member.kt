package digdaserver.domain.member.domain.entity

import digdaserver.domain.heritage.domain.entity.Heritage
import digdaserver.domain.oauth2.domain.entity.SocialProvider
import digdaserver.domain.point.domain.entity.Point
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@Entity
@Table(name = "member")
@DynamicUpdate
class Member(

    @Id
    @Column(unique = true, nullable = false)
    var id: String,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var profile: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider", nullable = false)
    var socialProvider: SocialProvider,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role,

    @Column(nullable = false)
    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false, name = "name_flag")
    var nameFlag: Boolean = false,

    @Column(nullable = true)
    var password: String? = null
) {

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    var heritages: MutableList<Heritage> = mutableListOf()

    @OneToOne(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    var point: Point? = null

    // 기존 Builder 대응용 secondary constructor
    constructor(
        id: String,
        email: String,
        name: String,
        profile: String?,
        socialProvider: SocialProvider,
        role: Role
    ) : this(
        id = id,
        email = email,
        name = name,
        profile = profile,
        socialProvider = socialProvider,
        role = role,
        createdAt = LocalDateTime.now(),
        nameFlag = false
    )

    fun updateEmail(newEmail: String) {
        this.email = newEmail
    }

    fun updateName(newName: String) {
        this.name = newName
    }

    fun updateProfile(newProfile: String?) {
        this.profile = newProfile
    }

    fun updateIsActivateNameFlag() {
        this.nameFlag = true
    }

    @PrePersist
    fun onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now()
        }
    }
}
