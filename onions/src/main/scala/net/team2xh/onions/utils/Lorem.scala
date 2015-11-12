package net.team2xh.onions.utils

object Lorem {

  var index = 0

  private val lines = Seq(
    "Etiam sit amet lacinia quam, sed efficitur lorem. Integer sit amet diam at tortor molestie pellentesque eget euismod lorem. Vivamus varius purus sed ex aliquam lacinia.",
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque enim erat, sollicitudin vitae lacus nec, consectetur porta velit. Donec porta placerat lectus, in porttitor elit.",
    "Curabitur eget lectus at elit suscipit feugiat. Donec egestas congue magna, et lacinia magna porta a. Praesent porta accumsan congue. Etiam maximus varius neque, non mollis lacus molestie eget.",
    "Nam ut odio ex. Donec faucibus in odio in blandit. In mattis sodales mi, quis rhoncus lorem tincidunt sed. Ut varius gravida augue at tristique. Suspendisse lacinia mi nunc, ac convallis elit consequat a.",
    "Ut sapien sit amet tortor rhoncus hendrerit nec et velit. In id erat in risus accumsan pellentesque at eget lacus. Curabitur quis purus non ante sagittis sagittis vel ullamcorper elit.",
    "Proin pretium consequat leo sit amet pharetra. Aenean convallis, est quis faucibus dapibus. Non efficitur purus faucibus. Vestibulum quis massa fringilla neque consequat bibendum."
  )

  def Ipsum: String = {
    val result = lines(index)
    index = (index + 1) % lines.length
    result
  }
}
