import time
import serial.tools.list_ports

ser = serial.Serial(port="COM3", baudrate=115200)

while True:
    numByte = ser.inWaiting()
    if numByte:
        payload = ser.read(numByte)
        ser.write(payload)

    time.sleep(1)
