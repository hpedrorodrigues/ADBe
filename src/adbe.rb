#!/usr/bin/env ruby

class TypeOptions
  class << self
    def quit; 'quit' end
    def simple_quit; 'q' end
  end
end

class Messages
  class << self
    def usage; 'You must pass some argument' end
    def multiple_devices; 'You have more than one device. In which device will be run?' end
    def type_device_number; 'Enter "device number" to run on a device' end
    def type_cancel; 'Enter "quit" to exit' end
    def invalid_option; 'Invalid option' end
    def invalid_device; 'Invalid device' end
  end
end

class Commands
  class << self
    def independents; %w(devices version start-server kill-server connect disconnect help) end
    def devices_info; 'adb devices -l' end
    def devices; 'adb devices' end
    def adb; 'adb' end
    def run_on_device; 'adb -s' end
  end
end

class Util
  class << self
    def is_number (string)
      true if Float(string) rescue false
    end
  end
end

class ADBe
  class << self
    def serials
      %x( #{Commands.devices} ).lines.map(&:chomp).select { |l| !l.empty? }.drop(1).map { |l| l.split("\t")[0]}
    end
    def devices_info
      %x( #{Commands.devices_info} ).lines.map(&:chomp).select { |l| !l.empty? }.drop(1)
    end
    def defer_to_adb(args)
      exec "#{Commands.adb} #{args.reduce("") { |cur, nex| cur += nex} }"
    end
    def run_on_device(args, serial)
      exec "#{Commands.run_on_device} #{serial} #{args.reduce("") { |cur, nex| cur += nex} }"
    end
    def handle_to_multiple_devices(args)
      puts
      puts Messages.multiple_devices
      puts

      devices_info = ADBe.devices_info

      devices_info.each_with_index { |value, key|
        device = value.split("\s")
        device_name = device[3]
        device_model = device[4]
        device_id = device[0]
        puts "#{key + 1} -> #{device_id} - #{device_name} ** #{device_model}"
      }

      puts
      puts Messages.type_device_number
      puts Messages.type_cancel

      selected_option = STDIN.gets.chomp

      if Util.is_number selected_option
        index = selected_option.to_i - 1
        if index < devices_info.length && index >= 0
          device = devices_info[index].split("\s")
          ADBe.run_on_device(args, device[0])
        else
          puts Messages.invalid_device
        end
      else
        case selected_option
          when TypeOptions.quit, TypeOptions.simple_quit
            puts
          else
            puts Messages.invalid_option
        end
      end
    end
  end
end

class Runner
  class << self
    def main(args)
      if args.empty?
        puts Messages.usage
      elsif Commands.independents.include? args[0] or ADBe.serials.length == 1
        ADBe.defer_to_adb args
      elsif ADBe.serials.length > 1
        ADBe.handle_to_multiple_devices args
      end
    end
  end
end

Runner.main ARGV