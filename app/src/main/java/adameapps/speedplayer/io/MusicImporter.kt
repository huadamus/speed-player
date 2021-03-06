package adameapps.speedplayer.io

import adameapps.speedplayer.model.MusicFile
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

object MusicImporter {

    fun getMusic(context: Context): List<MusicFile> {
        val output = mutableListOf<MusicFile>()
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)) != 0) {
                    val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    output.add(
                        MusicFile(
                            name, ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                cursor.getLong(
                                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                                )
                            )
                        )
                    )
                }
            }
        }
        output.sortBy {
            it.title
        }
        return output
    }
}
