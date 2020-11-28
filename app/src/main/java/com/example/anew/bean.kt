package com.example.anew

data class bean(
    val ErrorObj: Any,
    val Msg: Any,
    val ResultObj: ResultObj,
    val Status: Int,
    val StatusCode: Int
)

data class ResultObj(
    val AccessToken: String,
    val AccessTokenErrCode: Int,
    val CollegeID: Int,
    val CollegeName: String,
    val DataToken: String,
    val Email: String,
    val Gender: Boolean,
    val ReturnUrl: Any,
    val RoleID: Int,
    val RoleName: String,
    val Telphone: String,
    val UserID: Int,
    val UserName: String
)