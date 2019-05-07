package com.netsoftware.wallhaven.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.netsoftware.wallhaven.utility.managers.MyDisplayManager
import java.util.*

@Entity(tableName = "wallpaper")
data class Wallpaper(
    @PrimaryKey
    @SerializedName("id")
    var id: String = UUID.randomUUID().toString(),

    @SerializedName("path")
    var url: String = "",

    //TODO: Change to normal relation
    @SerializedName("uploader")
    @Embedded(prefix = "user_")
    var uploader: User = User(),

    var views: Int = 0,

    var favorites: Int = 0,

    @SerializedName("purity")
    var purity: String = PURITY_SFW,

    @SerializedName("category")
    var category: String = CATEGORY_GENERAL,

    @SerializedName("resolution")
    var resolution: String = "",

    @Expose
    var ratio: String = MyDisplayManager.findRatio(resolution),

    @SerializedName("file_size")
    var fileSize: String = "",

    @SerializedName("file_type")
    var fileType: String = "",

    @SerializedName("created_at")
    var creationDate: Date = Date(),

    @SerializedName("colors")
    var colors: MutableList<String> = mutableListOf(),

    @SerializedName("thumbs")
    var thumbs: MutableMap<String, String> = mutableMapOf()
//    var tags: MutableList<Tag> = mutableListOf()
)
//
//"id": "7zy509",
//"url": "http://stest39.wallhaven.cc/w/7zy509",
//"short_url": "http://whvn.cc/7zy509",
//"uploader": {
//    "username": "Berntsen",
//    "group": "Senior Moderator",
//    "avatar": {
//        "200px": "http://stest39.wallhaven.cc/images/user/avatar/200/default-avatar.jpg",
//        "128px": "http://stest39.wallhaven.cc/images/user/avatar/128/default-avatar.jpg",
//        "32px": "http://stest39.wallhaven.cc/images/user/avatar/32/default-avatar.jpg",
//        "20px": "http://stest39.wallhaven.cc/images/user/avatar/20/default-avatar.jpg"
//    }
//},
//"views": 13,
//"favorites": 1,
//"source": "",
//"purity": "sfw",
//"category": "general",
//"dimension_x": 1920,
//"dimension_y": 1080,
//"resolution": "1920x1080",
//"ratio": "1.78",
//"file_size": 2975731,
//"file_type": "image/png",
//"created_at": {
//    "date": "2018-09-13 19:40:08.000000",
//    "timezone_type": 3,
//    "timezone": "UTC"
//},
//"colors": [
//"#66cccc",
//"#424153",
//"#abbcda",
//"#0099cc",
//"#000000"
//],
//"path": "http://w.stest39.wallhaven.cc/full/7z/wallhaven-7zy509.png",
//"thumbs": {
//    "large": "http://th.stest39.wallhaven.cc/lg/7z/7zy509.jpg",
//    "original": "http://th.stest39.wallhaven.cc/orig/7z/7zy509.jpg",
//    "small": "http://th.stest39.wallhaven.cc/small/7z/7zy509.jpg"
//},
//"tags": [
//{
//    "id": 26,
//    "name": "Digital Art",
//    "alias": "",
//    "category_id": 25,
//    "category": "Digital",
//    "purity": "sfw",
//    "created_at": {
//    "date": "2018-09-13 19:34:59.000000",
//    "timezone_type": 3,
//    "timezone": "UTC"
//}
//},
//{
//    "id": 38,
//    "name": "Lake",
//    "alias": "",
//    "category_id": 41,
//    "category": "Landscapes",
//    "purity": "sfw",
//    "created_at": {
//    "date": "2018-09-13 19:39:26.000000",
//    "timezone_type": 3,
//    "timezone": "UTC"
//}
//},
//{
//    "id": 39,
//    "name": "Mountain",
//    "alias": "Mountains",
//    "category_id": 41,
//    "category": "Landscapes",
//    "purity": "sfw",
//    "created_at": {
//    "date": "2018-09-13 19:39:37.000000",
//    "timezone_type": 3,
//    "timezone": "UTC"
//}
//}
//]