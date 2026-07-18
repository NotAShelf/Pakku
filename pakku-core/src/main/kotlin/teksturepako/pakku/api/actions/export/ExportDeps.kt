package teksturepako.pakku.api.actions.export

import com.github.michaelbull.result.Result
import teksturepako.pakku.api.actions.errors.ActionError
import teksturepako.pakku.api.http.requestByteArray
import teksturepako.pakku.api.projects.ProjectFile

data class ExportDeps(
    val resolveContent: suspend (ProjectFile) -> Result<ByteArray, ActionError>?,
)

fun defaultExportDeps() = ExportDeps(
    resolveContent = ::resolveExportContentFromRemote,
)

suspend fun resolveExportContentFromRemote(file: ProjectFile): Result<ByteArray, ActionError>? =
    file.url?.let { requestByteArray(it) }
