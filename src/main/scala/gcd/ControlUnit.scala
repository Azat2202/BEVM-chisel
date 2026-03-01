package gcd

import chisel3._
import chisel3.simulator.EphemeralSimulator
import chisel3.simulator.EphemeralSimulator.toTestableClock
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemoryLoadFileType
import gcd.ControlUnit.initFlagsWithFalseValues

class ControlUnitOutput extends Bundle {
  val HALT = Bool()
}

class ControlUnit(memoryFileName: String) extends Module {
  val top: Top                           = Module(new Top(memoryFileName))
  val controlUnitOutput                  = IO(Output(new ControlUnitOutput))
  val topFlags                           = IO(Output(new Flags()))
  val microCodeMemory: SyncReadMem[UInt] = SyncReadMem(256, UInt(40.W))
  val MP                                 = RegInit(UInt(4.W), 0.U)
  val MR                                 = RegInit(UInt(40.W), 0.U)

  loadMemoryFromFileInline(
    microCodeMemory,
    getClass.getResource("/microcode.txt").getPath
  )

  controlUnitOutput.HALT        := false.B
  top.topInput.writeToRegisters := false.B
  top.topInput.data             := false.B

  MR := microCodeMemory.do_read(MP)
  printf(cf"MP = $MP%x, MR = $MR%x\n")
  // regs read
  topFlags.RDDR := MR(0)
  topFlags.RDCR := MR(1)
  topFlags.RDIP := MR(2)
  topFlags.RDSP := MR(3)
  topFlags.RDAC := MR(4)
  topFlags.RDBR := MR(5)
  topFlags.RDPS := MR(6)
  topFlags.RDIR := MR(7)

  when(MR(39)) {
    // ALU
    topFlags.COMR := MR(8)
    topFlags.COML := MR(9)
    topFlags.PLS1 := MR(10)
    topFlags.SORA := MR(11)

    // commutator
    topFlags.LTOL := MR(12)
    topFlags.LTOH := MR(13)
    topFlags.HTOL := MR(14)
    topFlags.HTOH := MR(15)
    topFlags.SEXT := MR(16)
    topFlags.SHLT := MR(17)
    topFlags.SHL0 := MR(18)
    topFlags.SHRT := MR(19)
    topFlags.SHRF := MR(20)
    topFlags.SETC := MR(21)
    topFlags.SETV := MR(22)
    topFlags.SETZ := MR(23) // todo: STNZ instead of SETZ
    // regs write
    topFlags.WRDR := MR(24)
    topFlags.WRCR := MR(25)
    topFlags.WRIP := MR(26)
    topFlags.WRSP := MR(27)
    topFlags.WRAC := MR(28)
    topFlags.WRBR := MR(29)
    topFlags.WRPS := MR(30)
    topFlags.WRAR := MR(31)

    // memory
    topFlags.LOAD := MR(32)
    topFlags.STOR := MR(33)

    // io
//    topFlags.IO := MR(0)
//    topFlags.INTS := MR(0)

    // reserved
//    topFlags.RDDR := MR(0)
//    topFlags.RDDR := MR(0)

    controlUnitOutput.HALT := MR(38)
  }.otherwise {
    initFlagsWithFalseValues(topFlags)
    val toCompareData  = top.topOutput.data(8, 0)
    val toCompareInput = MR(22, 15)
    val compareBit     = (toCompareData | toCompareInput) > 0.U
    when(compareBit ^ MR(31)) {
      MP := MR(30, 23)
    }.otherwise(MP := MP + 1.U)
  }

  top.flags := topFlags

}

object ControlUnit {
  def initFlagsWithFalseValues(topFlags: Flags) = {
    topFlags.COMR := false.B
    topFlags.COML := false.B
    topFlags.PLS1 := false.B
    topFlags.SORA := false.B

    // commutator
    topFlags.LTOL := false.B
    topFlags.LTOH := false.B
    topFlags.HTOL := false.B
    topFlags.HTOH := false.B
    topFlags.SEXT := false.B
    topFlags.SHLT := false.B
    topFlags.SHL0 := false.B
    topFlags.SHRT := false.B
    topFlags.SHRF := false.B
    topFlags.SETC := false.B
    topFlags.SETV := false.B
    topFlags.SETZ := false.B // todo: STNZ instead of SETZ
    // regs write
    topFlags.WRDR := false.B
    topFlags.WRCR := false.B
    topFlags.WRIP := false.B
    topFlags.WRSP := false.B
    topFlags.WRAC := false.B
    topFlags.WRBR := false.B
    topFlags.WRPS := false.B
    topFlags.WRAR := false.B

    // memory
    topFlags.LOAD := false.B
    topFlags.STOR := false.B
  }
}

//object Main extends App {
//  EphemeralSimulator.simulate(new ControlUnit()) { c =>
//    while(!c.controlUnitOutput.HALT.litToBoolean){
//      c.clock.step()
//    }
//  }
//}
