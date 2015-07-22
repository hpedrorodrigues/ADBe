#!/bin/sh
exec scala -savecompiled "$0" "$@"
!#

import scala.sys.process._
import scala.io.StdIn._

object TypeOptions {
  val All = "all"
  val Quit = "quit"
  val SimpleAll = "a"
  val SimpleQuit = "q"
}

object Messages {

  val Usage = "You must pass some argument"
  val MultipleDevices = "You have more than one device. In which device will be run?"
  val TypeDeviceNumber = "Enter 'device number' to run on a device"
  val TypeAllDevices = "Enter 'all' to run on all devices"
  val TypeCancel = "Enter 'quit' to exit"
  val InvalidOption = "Invalid option"
  val InvalidDevice = "Invalid device"
}

object Commands {

  val Independents = List("devices", "version", "start-server", "kill-server", "connect", "disconnect", "help")
  val DevicesInfo = Seq("adb", "devices", "-l")
  val Devices = Seq("adb", "devices")
  val ADB = Seq("adb")
  val RunOnDevice = Seq("adb", "-s")
}

object Util {

  def isNumber(x: String) = x forall Character.isDigit
}

object ADBe {

  def serials: List[String] = {
    val processOutput = Commands.Devices.!!
    (processOutput split "\n").toList.tail.map(_.split("\\s").head)
  }

  def devicesInfo: List[String] = {
    val processOutput = Commands.DevicesInfo.!!
    (processOutput split "\n").toList.tail
  }

  def deferToAdb(args: List[String]) = {
    (Commands.ADB ++ args.flatMap(Seq(_))).!
  }

  def runOnDevice(args: List[String], serial: String) {
    (Commands.RunOnDevice ++ Seq(serial) ++ args.flatMap(Seq(_))).!
  }

  def handleToMultipleDevices(args: List[String]) = {
    println()
    println(Messages.MultipleDevices)
    println()

    val devicesInfo = ADBe.devicesInfo

    for ((value, key) <- devicesInfo.zipWithIndex) {
      val device = value.split("\\s").toList.filter(!_.isEmpty)
      val deviceName = device(3)
      val deviceModel = device(4)
      val deviceId = device.head
      println(s"${key + 1} -> $deviceId - $deviceName ** $deviceModel")
    }

    println()
    println(Messages.TypeDeviceNumber)
    println(Messages.TypeAllDevices)
    println(Messages.TypeCancel)

    val selectedOption = readLine()

    if (Util.isNumber(selectedOption)) {
      val index = selectedOption.toInt - 1
      if (index < devicesInfo.length && index >= 0) {
        val device = devicesInfo(index).split("\\s").filter(!_.isEmpty)
        ADBe.runOnDevice(args, device.head)
      } else {
        println(Messages.InvalidDevice)
      }
    } else {
      selectedOption match {
        case TypeOptions.All | TypeOptions.SimpleAll =>
          for (index <- devicesInfo) {
            val device = index.split("\\s").filter(!_.isEmpty)
            ADBe.runOnDevice(args, device.head)
          }
        case TypeOptions.Quit | TypeOptions.SimpleQuit => println()
        case _ => println(Messages.InvalidOption)
      }
    }
  }
}

object Runner {

  def main(args: Array[String]) {

    val argsList = args.toList
    if (args.length == 0) {
      println(Messages.Usage)
    } else if (Commands.Independents.contains(argsList.head) || ADBe.serials.size == 1) {
      ADBe.deferToAdb(argsList)
    } else if (ADBe.serials.size > 1) {
      ADBe.handleToMultipleDevices(argsList)
    }
  }
}

Runner.main(args)