package me.avo.spottit.exception

class InvalidRefreshTokenException : IllegalArgumentException(
    "An invalid refresh token has been provided."
)
