package github.dqw4w9wgxcq.botapi.commons

open class RetryException(
    message: String,
    cause: Throwable? = null,
    val retries: Int = 10,
) : RuntimeException(message, cause) {
    constructor(message: String) : this(message, null)

    init {
        require(retries >= 1)
    }
}

//doesn't get logged at INFO level
open class SilentException(message: String) : RetryException(message)

open class NotFoundException(message: String) : RetryException(message)

class WaitTimeoutException(
    timeout: Int,
    pollRate: Int,
    supply: () -> Any?,
    condition: (Any?) -> Boolean,
) : RetryException("timeout:$timeout pollRate:$pollRate condition:$condition supply:$supply", retries = 10)

class FatalException(message: String?, cause: Throwable? = null) : RuntimeException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}