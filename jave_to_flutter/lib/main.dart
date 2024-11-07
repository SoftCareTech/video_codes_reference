import 'package:flutter/material.dart';
import 'package:flutter/services.dart';




//Send and receive a response
// receive a responses from the other after a time passed


void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(

        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = MethodChannel('samples.flutter.dev/battery');

  @override
  void initState() {
    super.initState();
   // startBackgroundTimer();
    platform.setMethodCallHandler(_handleMethodCall);
  }

  Future<void> startBackgroundTimer() async {
    try {
      await platform.invokeMethod('startBackgroundTimer');
    } catch (e) {
      print("Failed to start background timer: $e");
    }
  }

  Future<void> stopBackgroundTimer() async {
    try {
      await platform.invokeMethod('stopBackgroundTimer');
    } catch (e) {
      print("Failed to stop background timer: $e");
    }
  }
  Future<void> getString() async {
    try {
      var v=   await platform.invokeMethod('getString');
      print("Value from call $v");
    } catch (e) {
      print("Failed to start background timer: $e");
    }
  }
  Future<dynamic> _handleMethodCall(MethodCall call) async {
    if (call.method == "updateBatteryLevel") {
      _counter++;
      setState(() {
        _batteryLevel = "Battery level: ${call.arguments}% $_counter";
      });
    }else if("getString"==call.method ){
      setState(() {
        _batteryLevel = "String: ${call.arguments} ";
      });
    }

  }

  @override
  void dispose() {
    stopBackgroundTimer();
    super.dispose();
  }


  // Get battery level.
  String _batteryLevel = 'Unknown battery level.';

  Future<void> _getBatteryLevel() async {
    String batteryLevel;
    try {
      final result = await platform.invokeMethod<int>('getBatteryLevel');
      batteryLevel = 'Battery level at $result % .';
    } on PlatformException catch (e) {
      batteryLevel = "Failed to get battery level: '${e.message}'.";
    }

    setState(() {
      _batteryLevel = batteryLevel;
    });
  }






  int _counter = 0;
  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'You have pushed the button this many times:',
            ),
            Text(
              '$_batteryLevel',
              style: Theme.of(context).textTheme.headlineMedium,
            ),
            ElevatedButton(
              onPressed: getString,
              child: const Text(' getString'),
            ),
            ElevatedButton(
              onPressed: startBackgroundTimer,
              child: const Text(' startBackgroundTimer'),
            ),

            ElevatedButton(
              onPressed: stopBackgroundTimer,
              child: const Text('Stop'),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
