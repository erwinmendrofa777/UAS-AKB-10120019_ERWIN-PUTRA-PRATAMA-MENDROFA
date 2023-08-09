package dev.erwin.todo.core.data.repositories

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.erwin.todo.R
import dev.erwin.todo.core.domain.models.StudentProfile
import dev.erwin.todo.core.domain.repositories.StudentRepository
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context
) : StudentRepository {
    override val studentProfile: StudentProfile
        get() = StudentProfile(
            studentId = context.getString(R.string.dummy_student_id),
            fullName = context.getString(R.string.dummy_student_name),
            profilePicture = AppCompatResources.getDrawable(context, R.drawable.dummy_profile_picture)!!,
            `class` = context.getString(R.string.dummy_class),
            emailAddress = context.getString(R.string.dummy_email_address)
        )
}