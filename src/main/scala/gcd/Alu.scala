package gcd

import chisel3._

class AluInput extends Bundle {
  val left  = SInt(16.W)
  val right = SInt(16.W)
  val C     = Bool()
}

class AluOutput extends Bundle {
  val data = SInt(16.W)
  val C    = Bool()
  val C0   = Bool()
  val C14  = Bool()
}

class AluFlags extends Bundle {
  val COML = Bool()
  val SORA = Bool()
  val COMR = Bool()
  val PLS1 = Bool()
}

class Alu extends Module {

  val input  = IO(Input(new AluInput()))
  val output = IO(Output(new AluOutput()))
  val flags  = IO(Input(new AluFlags()))

  private val leftInv = Wire(SInt(16.W))
  when(flags.COML) {
    leftInv := ~input.left
  }.otherwise(leftInv := input.left)

  private val rightInv = Wire(SInt(16.W))
  when(flags.COMR) {
    rightInv := ~input.right
  }.otherwise(rightInv := input.right)

  private val pls1Wire = Wire(UInt(16.W))
  when(flags.PLS1) {
    pls1Wire := 1.U
  }.otherwise(pls1Wire := 0.U)

  when(flags.SORA) {
    // and
    output.data := (leftInv & rightInv) + pls1Wire.asSInt
    output.C    := 0.U
    output.C14  := 0.U
  }.otherwise {
    val result = leftInv.asUInt +& rightInv.asUInt + pls1Wire
    output.data := result.asSInt
    output.C    := result(16)
    val c14Wire = leftInv(14, 0).asUInt +& rightInv(14, 0).asUInt + pls1Wire
    output.C14 := c14Wire(15)
  }
  output.C0 := input.C
}
