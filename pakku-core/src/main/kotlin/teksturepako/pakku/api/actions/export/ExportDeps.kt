package teksturepako.pakku.api.actions.export

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import teksturepako.pakku.api.actions.errors.ActionError
import teksturepako.pakku.api.http.requestByteArray
import teksturepako.pakku.api.http.requireHttpsWhenUnverifiable
import teksturepako.pakku.api.projects.ProjectFile

data class ExportDeps(
    val resolveContent: suspend (ProjectFile) -> Result<ByteArray, ActionError>?,
)

fun defaultExportDeps() = ExportDeps(
    resolveContent = ::resolveExportContentFromRemote,
)

suspend fun resolveExportContentFromRemote(file: ProjectFile): Result<ByteArray, ActionError>?
{
    val url = file.url ?: return null
    requireHttpsWhenUnverifiable(url, file.hashes)?.let { return Err(it) }
    return requestByteArray(url)
}
