package gcd

import chisel3._

class AluInput extends Bundle{
  val left = UInt(16.W)
  val right = UInt(16.W)
  val C = Bool()
}

class AluOutput extends Bundle {
  val data = UInt(16.W)
  val C = Bool()
  val C0 = Bool()
  val C14 = Bool()
}

class AluFlags extends Bundle{
  val COML = Bool()
  val SORA = Bool()
  val COMR = Bool()
  val PLS1 = Bool()
}

class Alu extends Module {

  val input = IO(Input(new AluInput()))
  val output = IO(Output(new AluOutput()))
  val flags = IO(Input(new AluFlags()))

  private val leftInv = Wire(UInt(16.W))
  when(flags.COML){
    leftInv := ~input.left
  }.otherwise(leftInv := input.left)

  private val rightInv = Wire(UInt(16.W))
  when(flags.COMR){
    rightInv := ~input.right
  }.otherwise(rightInv := input.right)

  private val pls1Wire = Wire(UInt(1.W))
  when(flags.PLS1){
    pls1Wire := 1.U
  }.otherwise(pls1Wire := 0.U)

  when(flags.SORA){
    // and
    output.data := leftInv & rightInv + pls1Wire
    output.C := 0.U
    output.C14 := 0.U
  }.otherwise{
    val result = leftInv +& rightInv + pls1Wire
    output.data := result
    output.C := result(16)
    val c14Wire = leftInv(14, 0) +& rightInv(14, 0) + pls1Wire
    output.C14 := c14Wire(15)
  }
  output.C0 := input.C
}
