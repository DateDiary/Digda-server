package digdaserver.domain.group.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "group_member")
class GroupMember(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    val group: Group,

    @Column(nullable = false)
    val memberId: String,

    @Column(nullable = false)
    var memberName: String,

    @Column(nullable = true)
    var memberProfile: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: GroupRole,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val joinedAt: LocalDateTime = LocalDateTime.now()
)
