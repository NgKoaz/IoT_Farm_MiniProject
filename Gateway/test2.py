import time
import serial.tools.list_ports

def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"
    for i in range(0, N):
        port = ports[i]
        strPort = str(port)
        if "USB" in strPort:
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
    return commPort
    # return "/dev/ttyUSB1"

portName = getPort()
print(portName)



try:
    ser = serial.Serial(port=portName, baudrate=9600)
    print("Open successfully")
except:
    print("Can not open the port")

mixer1_ON  = [1, 6, 0, 0, 0, 255, 201, 138]
mixer1_OFF = [1, 6, 0, 0, 0, 0, 137, 202]

def setMixer1(state):
    if state == True:
        ser.write(mixer1_ON)
    else:
        ser.write(mixer1_OFF)
    time.sleep(1)
    print(serial_read_data(ser))

mixer2_ON  = [2, 6, 0, 0, 0, 255, 201, 185]
mixer2_OFF = [2, 6, 0, 0, 0, 0, 137, 249]

def setMixer2(state):
    if state == True:
        ser.write(mixer2_ON)
    else:
        ser.write(mixer2_OFF)
    time.sleep(1)
    print(serial_read_data(ser))

mixer3_ON  = [3, 6, 0, 0, 0, 255, 200, 104]
mixer3_OFF = [3, 6, 0, 0, 0, 0, 136, 40]

def setMixer3(state):    
    if state == True:
        ser.write(mixer3_ON)
    else:
        ser.write(mixer3_OFF)
    time.sleep(1)
    print(serial_read_data(ser))

selector1_ON  = [4, 6, 0, 0, 0, 255, 201, 223]
selector1_OFF = [4, 6, 0, 0, 0, 0, 137, 159]

def setSelector1(state):    
    if state == True:
        ser.write(selector1_ON)
    else:
        ser.write(selector1_OFF)
    time.sleep(1)
    print(serial_read_data(ser))

selector2_ON  = [5, 6, 0, 0, 0, 255, 200, 14]
selector2_OFF = [5, 6, 0, 0, 0, 0, 136, 78]

def setSelector2(state):    
    if state == True:
        ser.write(selector2_ON)
    else:
        ser.write(selector2_OFF)
    time.sleep(1)
    print(serial_read_data(ser))

selector3_ON  = [6, 6, 0, 0, 0, 255, 200, 61]
selector3_OFF = [6, 6, 0, 0, 0, 0, 136, 125]

def setSelector3(state):    
    if state == True:
        ser.write(selector3_ON)
    else:
        ser.write(selector3_OFF)
    time.sleep(1)
    print(serial_read_data(ser))

pumpIn_ON  = [7, 6, 0, 0, 0, 255, 201, 236]
pumpIn_OFF = [7, 6, 0, 0, 0, 0, 137, 172]

def setPumpIn(state):    
    if state == True:
        ser.write(pumpIn_ON)
    else:
        ser.write(pumpIn_OFF)
    time.sleep(1)
    print(serial_read_data(ser))

pumpOut_ON  = [8, 6, 0, 0, 0, 255, 201, 19]
pumpOut_OFF = [8, 6, 0, 0, 0, 0, 137, 83]

def setPumpOut(state):    
    if state == True:
        ser.write(pumpOut_ON)
    else:
        ser.write(pumpOut_OFF)
    time.sleep(1)
    print(serial_read_data(ser))

def serial_read_data(ser):
    bytesToRead = ser.inWaiting()
    if bytesToRead > 0:
        out = ser.read(bytesToRead)
        data_array = [b for b in out]
        print(data_array)
        if len(data_array) >= 7:
            array_size = len(data_array)
            value = data_array[array_size - 4] * 256 + data_array[array_size - 3]
            return value
        else:
            return -1
    return 0

soil_temperature =[1, 3, 0, 6, 0, 1, 100, 11]
def readTemperature():
    serial_read_data(ser)
    ser.write(soil_temperature)
    time.sleep(1)
    return serial_read_data(ser)

soil_moisture = [1, 3, 0, 7, 0, 1, 53, 203]
def readMoisture():
    serial_read_data(ser)
    ser.write(soil_moisture)
    time.sleep(1)
    return serial_read_data(ser)

while True:
    print("TEST MOISTURE")
    print(readMoisture())
    time.sleep(1)
    print("TEST TEMPERATURE")
    print(readTemperature())
    time.sleep(1)
    print("TEST MIXER 1")
    setMixer1(True)
    time.sleep(1)
    print("TEST MIXER 2")
    setMixer2(True) 
    time.sleep(1)
    print("TEST MIXER 3")
    setMixer3(True)
    time.sleep(1)
    print("TEST SELECTOR 1")
    setSelector1(True)
    time.sleep(1)
    print("TEST SELECTOR 2")
    setSelector2(True)
    time.sleep(1)
    print("TEST SELECTOR 3")
    setSelector3(True)
    time.sleep(1)
    print("TEST PUMP IN")
    setPumpIn(True)
    time.sleep(1)
    print("TEST PUMP OUT")
    setPumpOut(True)
    time.sleep(1)