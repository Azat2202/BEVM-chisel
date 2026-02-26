package gcd

import chisel3._
import chisel3.util.Cat

case class PSBits(
    P: Bool,    // program
    W: Bool,    // write
    INT: Bool,  // interrupt request
    EI: Bool,   // interrupts enable
    ZERO: Bool, // zero
    N: Bool,
    Z: Bool,
    V: Bool,
    C: Bool
)

object PSBits {
  def fromPS(ps: UInt): PSBits =
    PSBits(
      ps(8),
      ps(7),
      ps(6),
      ps(5),
      ps(4),
      ps(3),
      ps(2),
      ps(1),
      ps(8)
    )
}

class RegisterSetFlags extends Bundle {
  val WRAC = Bool()
  val RDAC = Bool()
  val WRBR = Bool()
  val RDBR = Bool()
  val WRPS = Bool()
  val RDPS = Bool()
  val RDIR = Bool()
  val WRDR = Bool()
  val RDDR = Bool()
  val WRCR = Bool()
  val RDCR = Bool()
  val WRIP = Bool()
  val RDIP = Bool()
  val WRSP = Bool()
  val RDSP = Bool()
  val WRAR = Bool()
  val LOAD = Bool()
  val STOR = Bool()
}

class RegisterSet extends Module {
  val flags  = IO(Input(new RegisterSetFlags()))
  val input = IO(Input(new ResultAttributesBlockOutput()))
  val output = IO(Output(new AluInput()))
  val memoryInput = IO(Output(new MemoryInput()))
  val memoryOutput = IO(Input(new MemoryOutput()))

  // left side
  private val AC = RegInit(UInt(16.W), 0.U)
  private val BR = RegInit(UInt(16.W), 0.U)
  private val PS = RegInit(UInt(16.W), 0.U)
  private val IR = RegInit(UInt(16.W), 0.U)

  // right side
  private val DR = RegInit(UInt(16.W), 0.U)
  private val CR = RegInit(UInt(16.W), 0.U)
  private val IP = RegInit(UInt(11.W), 0.U)
  private val SP = RegInit(UInt(11.W), 0.U)

  private val AR = RegInit(UInt(11.W), 0.U)

  // todo: memory IO

  output.left  := 0.S
  output.right := 0.S
  output.C := PSBits.fromPS(PS).C
  PS := PS | Cat(input.N, input.Z, input.V, input.C) // setting nzvc after operation
  memoryInput.address := AR
  memoryInput.write := false.B
  memoryInput.data := 0.U

  when(flags.RDAC)(output.left := AC.asSInt)
  when(flags.RDBR)(output.left := BR.asSInt)
  when(flags.RDPS)(output.left := PS.asSInt)
  when(flags.RDIR)(output.left := IR.asSInt)

  when(flags.RDDR)(output.right := DR.asSInt)
  when(flags.RDCR)(output.right := CR.asSInt)
  when(flags.RDIP)(output.right := IP.pad(16).asSInt)
  when(flags.RDSP)(output.right := SP.pad(16).asSInt)

  when(flags.WRAC)(AC := input.data.asUInt)
  when(flags.WRBR)(BR := input.data.asUInt)
  when(flags.WRPS)(PS := input.data.asUInt)
  when(flags.WRDR)(DR := input.data.asUInt)
  when(flags.WRCR)(CR := input.data.asUInt)
  when(flags.WRIP)(IP := input.data.asUInt)
  when(flags.WRSP)(SP := input.data.asUInt)
  when(flags.WRAR)(AR := input.data.asUInt)

  // memory connection
  when(flags.LOAD){
    DR := memoryOutput.data
  }
  when(flags.STOR){
    memoryInput.data := DR
    memoryInput.write := true.B
  }

}
