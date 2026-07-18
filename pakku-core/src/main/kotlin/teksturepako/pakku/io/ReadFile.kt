package teksturepako.pakku.io

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import teksturepako.pakku.api.actions.errors.ActionError
import teksturepako.pakku.api.actions.errors.ErrorWhileReading
import teksturepako.pakku.api.data.PakkuException
import teksturepako.pakku.api.data.json
import kotlin.io.path.Path
import kotlin.io.path.readText

fun readPathTextOrNull(path: String): String?
{
    return runCatching { Path(path).readText() }.getOrNull()
}

inline fun <reified T> decodeOrNew(
    value: T, path: String, format: StringFormat = json
): Result<T, ActionError>
{
    val text = readPathTextOrNull(path) ?: return Ok(value)
    return runCatching { format.decodeFromString<T>(format.serializersModule.serializer(), text) }.fold(
        onSuccess = { Ok(it) },
        onFailure = { Err(ErrorWhileReading(path, it.message)) }
    )
}

inline fun <reified T> decodeToResult(
    path: String, format: StringFormat = json
): kotlin.Result<T> = readPathTextOrNull(path)?.let {
    runCatching { kotlin.Result.success(format.decodeFromString<T>(format.serializersModule.serializer(), it)) }.getOrElse { exception ->
        kotlin.Result.failure(PakkuException("Error occurred while reading '$path': ${exception.message}"))
    }
} ?: kotlin.Result.failure(PakkuException("Could not read '$path'"))
