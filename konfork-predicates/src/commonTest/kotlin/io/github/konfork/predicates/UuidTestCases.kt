package io.github.konfork.predicates

class UuidTestCases {
    private val invalid: List<String> = listOf(
        "",
        "@",
        "test@example.com",
        " 00000000-0000-0000-0000-000000000000",
        "00000000-0000-0000-0000-000000000000 ",
    )
    val nilUuid: List<String> = listOf("00000000-0000-0000-0000-000000000000")

    val validUuid =
        nilUuid + valid(1) + valid(2) + valid(3) + valid(4) + valid(5)

    val invalidUuid =
        invalid + invalid(1) + invalid(2) + invalid(3) + invalid(4) + invalid(5)

    private val all =
        invalidUuid + validUuid

    val validV1 = valid(1)
    val validV2 = valid(2)
    val validV3 = valid(3)
    val validV4 = valid(4)
    val validV5 = valid(5)

    val invalidNill = all - nilUuid.toSet()
    val invalidV1 = all - validV1.toSet()
    val invalidV2 = all - validV2.toSet()
    val invalidV3 = all - validV3.toSet()
    val invalidV4 = all - validV4.toSet()
    val invalidV5 = all - validV5.toSet()

    private fun valid(version: Int): List<String> =
        listOf(
            "c63f510c-6214-${version}1ed-9b6a-0242ac120002",
            "d1241f44-6214-${version}1ed-9b6a-0242ac120002",
        )

    private fun invalid(version: Int): List<String> =
        listOf(
            "c63f510c-6214-${version}1ed-cb6a-0242ac120002",
            "d1241f44-6214-${version}1ed-db6a-0242ac120002",
            " c63f510c-6214-${version}1ed-9b6a-0242ac120002",
            "d1241f44-6214-${version}1ed-9b6a-0242ac120002 ",
        )
}
