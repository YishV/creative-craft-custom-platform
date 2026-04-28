package com.ruoyi.system.domain.creative;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 评论互动对象 CreativeComment
 * 兼容 creative_comment 和 creative_interaction_comment 两张表
 */
public class CreativeComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long commentId;

    // --- 以下字段用于 creative_comment 表 (旧版/特定逻辑) ---
    /** 作品ID */
    private Long postId;
    /** 评论内容 (旧版名称) */
    private String commentContent;
    /** 审核状态 (旧版名称) */
    private String auditStatus;

    // --- 以下字段用于 creative_interaction_comment 表 (新版/通用交互) ---
    /** 目标类型(product-商品, post-社区帖子) */
    private String targetType;
    /** 目标ID */
    private Long targetId;
    /** 评论人昵称 */
    private String userName;
    /** 评论人头像 */
    private String avatar;
    /** 评论内容 */
    private String content;
    /** 父评论ID */
    private Long parentId;
    /** 评分(1-5星) */
    private Integer score;
    /** 状态(0显示 1隐藏) */
    private String status;
    /** 评论用户ID */
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public Long getCommentId() { return commentId; }

    public void setPostId(Long postId) { this.postId = postId; }
    public Long getPostId() { return postId; }

    public void setCommentContent(String commentContent) { this.commentContent = commentContent; }
    public String getCommentContent() { return commentContent; }

    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
    public String getAuditStatus() { return auditStatus; }

    public void setTargetType(String targetType) { this.targetType = targetType; }
    public String getTargetType() { return targetType; }

    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public Long getTargetId() { return targetId; }

    public void setUserId(Long userId) { this.userId = userId; }
    public Long getUserId() { return userId; }

    public void setUserName(String userName) { this.userName = userName; }
    public String getUserName() { return userName; }

    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getAvatar() { return avatar; }

    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }

    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getParentId() { return parentId; }

    public void setScore(Integer score) { this.score = score; }
    public Integer getScore() { return score; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setGmtCreate(Date gmtCreate) { this.gmtCreate = gmtCreate; }
    public Date getGmtCreate() { return gmtCreate; }

    @Override
    public String toString() {
        return "CreativeComment{" +
            "commentId=" + commentId +
            ", postId=" + postId +
            ", commentContent='" + commentContent + '\'' +
            ", auditStatus='" + auditStatus + '\'' +
            ", targetType='" + targetType + '\'' +
            ", targetId=" + targetId +
            ", userId=" + userId +
            ", userName='" + userName + '\'' +
            ", avatar='" + avatar + '\'' +
            ", content='" + content + '\'' +
            ", parentId=" + parentId +
            ", score=" + score +
            ", status='" + status + '\'' +
            ", gmtCreate=" + gmtCreate +
            "} " + super.toString();
    }
}
