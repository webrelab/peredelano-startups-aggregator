package com.repedelano.orm.vacancies

import com.repedelano.dtos.vacancy.VacancyStatus
import com.repedelano.orm.idea.Ideas
import com.repedelano.orm.projrctroles.ProjectRoles
import com.repedelano.orm.technology.Technologies
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object Vacancies : IntIdTable("vacancies") {

    val ideaId = reference("idea_id", Ideas.id)
    val projectRoleId = reference("project_role_id", ProjectRoles)
    val description = text("description")
    val status = enumerationByName("status", 50, VacancyStatus::class)
}

object VacancyTechnologies : Table("vacancy_technologies") {

    val vacancyId = reference("vacancy_id", Vacancies.id)
    val technologyId = reference("technology_id", Technologies.id)

    override val primaryKey = PrimaryKey(vacancyId, technologyId)
}