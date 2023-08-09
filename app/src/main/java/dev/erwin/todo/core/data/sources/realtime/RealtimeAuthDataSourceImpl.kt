package dev.erwin.todo.core.data.sources.realtime

import arrow.core.Either
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dev.erwin.todo.core.domain.models.AuthStudent
import dev.erwin.todo.core.utils.Failure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class RealtimeAuthDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleClient: GoogleSignInClient,
) : RealtimeAuthDataSource {
    override suspend fun signInWithEmailAndPassword(student: AuthStudent): Either<Exception, Nothing?> =
        withContext(Dispatchers.IO) {
            try {
                val (email, password) = student
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                Either.Right(null)
            } catch (exception: FirebaseAuthInvalidUserException) {
                Either.Left(exception)
            } catch (exception: FirebaseAuthInvalidCredentialsException) {
                Either.Left(exception)
            } catch (exception: IOException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    override suspend fun signUpWithEmailAndPassword(student: AuthStudent): Either<Exception, Nothing?> =
        withContext(Dispatchers.IO) {
            try {
                val (email, password) = student
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                Either.Right(null)
            } catch (exception: FirebaseAuthWeakPasswordException) {
                Either.Left(exception)
            } catch (exception: FirebaseAuthUserCollisionException) {
                Either.Left(exception)
            } catch (exception: IOException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    override suspend fun signInWithGoogle(googleToken: String?) = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAuthProvider.getCredential(googleToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Either.Right(null)
        } catch (exception: FirebaseAuthInvalidUserException) {
            Either.Left(exception)
        } catch (exception: FirebaseAuthUserCollisionException) {
            Either.Left(exception)
        } catch (exception: FirebaseAuthInvalidCredentialsException) {
            Either.Left(exception)
        } catch (exception: IOException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun signOut(): Unit = withContext(Dispatchers.IO) {
        googleClient.signOut().await()
        firebaseAuth.signOut()
    }

    override fun getUser(): Flow<FirebaseUser?> =
        callbackFlow {
            val authStateListener = AuthStateListener {
                trySend(it.currentUser)
            }
            firebaseAuth.addAuthStateListener(authStateListener)
            awaitClose {
                firebaseAuth.removeAuthStateListener(authStateListener)
            }
        }
}