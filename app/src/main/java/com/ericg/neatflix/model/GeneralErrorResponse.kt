package com.ericg.neatflix.model
 
data class GeneralErrorResponse(
    val message: String,
    val errors: Map<String, List<String>>? = null
) 