package com.repedelano.orm.idea

import com.repedelano.dtos.idea.IdeaStage
import com.repedelano.dtos.idea.IdeaStatus
import com.repedelano.orm.businessmodel.BusinessModels
import com.repedelano.orm.scope.Scopes
import com.repedelano.orm.technology.Technologies
import com.repedelano.orm.user.Users
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Ideas : UUIDTable(name = "ideas") {

    val created = timestamp("created")
    val updated = timestamp("updated")
    val owner = reference("owner", Users.id).nullable()
    val title = varchar("title", 100).nullable()
    val tgLink = varchar("tg_link", 100).nullable()
    val isFavorite = bool("is_favorite").default(false)
    val problem = text("problem").nullable()
    val description = text("description").nullable()
    val similarProjects = text("similar_projects").nullable()
    val targetAudience = text("target_audience").nullable()
    val marketResearch = text("market_research").nullable()
    val businessPlan = text("businessPlan").nullable()
    val resources = text("resources").nullable()
    val status = enumerationByName("status", 50, IdeaStatus::class)
    val stage = enumerationByName("stage", 50, IdeaStage::class)
}

object IdeaScopes : Table(name = "idea_copes") {

    val ideaId = reference("idea_id", Ideas.id, onDelete = ReferenceOption.CASCADE)
    val scopeId = reference("scope_id", Scopes.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(ideaId, scopeId)
}

object IdeaBusinessModels : Table(name = "idea-business-models") {

    val ideaId = reference("idea_id", Ideas.id, onDelete = ReferenceOption.CASCADE)
    val businessModelId = reference("business_model_id", BusinessModels.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(ideaId, businessModelId)
}

object IdeaTechnologies : Table(name = "idea-technologies") {

    val ideaId = reference("idea_id", Ideas.id, onDelete = ReferenceOption.CASCADE)
    val technologyId = reference("technology_id", Technologies.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(ideaId, technologyId)
}