package gcd

import chisel3._

class AluInput extends Bundle{
  val left = Input(UInt(16.W))
  val right = Input(UInt(16.W))
  val C = Input(Bool())
}

class AluOutput extends Bundle {
  val data = Output(UInt(16.W))
  val C = Output(Bool())
  val C0 = Output(Bool())
  val C14 = Output(Bool())
}

class AluFlags extends Bundle{
  val COML = Input(Bool())
  val SORA = Input(Bool())
  val COMR = Input(Bool())
  val PLS1 = Input(Bool())
}

class Alu extends Module {

  val input = IO(new AluInput())
  val output = IO(new AluOutput())
  val flags = IO(new AluFlags())

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
