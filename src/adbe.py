#! /bin/sh
""":"
exec python $0 ${1+"$@"}
"""

class Singleton(type):
    _instances = {}
    def __call__(cls, *args, **kwargs):
        if cls not in cls._instances:
            cls._instances[cls] = super(Singleton, cls).__call__(*args, **kwargs)
        return cls._instances[cls]

class Messages(object):
    __metaclass__ = Singleton

    USAGE = "You must pass some argument"
    MULTIPLE_DEVICES = "You have more than one device. In which device will be run?"
    TYPE_DEVICE_NUMBER = "Enter 'device number' to run on a device"
    TYPE_CANCEL = "Enter 'quit' to exit"
    INVALID_OPTION = "Invalid option"
    INVALID_DEVICE = "Invalid device"

class TypeOptions(object):
    __metaclass__ = Singleton

    QUIT = "quit"
    SIMPLE_QUIT = "q"

if __name__ == '__main__':
    print Messages.MULTIPLE_DEVICES