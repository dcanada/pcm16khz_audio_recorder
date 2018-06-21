import 'package:flutter/material.dart';
import 'dart:io';
import 'package:path_provider/path_provider.dart';
import 'dart:math';
import 'package:pcm16khz_audio_recorder/pcm16khz_audio_recorder.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: new Text('Plugin audio recorder'),
        ),
        body: new AppBody(),
      ),
    );
  }
}

class AppBody extends StatefulWidget{

  @override
  State<StatefulWidget> createState() => new AppBodyState();
}

class AppBodyState extends State<AppBody>{
  Recording _recording = new Recording();
  bool _isRecording = false;
  Random random = new Random();
  TextEditingController _controller = new TextEditingController();

  @override
  Widget build(BuildContext context) {
    return new Center(
      child: new Padding(
        padding: new EdgeInsets.all(8.0),
        child: new Column(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: <Widget>[
              new FlatButton(onPressed: _isRecording ? null : _start, child: new Text("Start"), color: Colors.green,),
              new FlatButton(onPressed: _isRecording ? _stop : null, child: new Text("Stop"), color: Colors.red,),
              new TextField(controller: _controller, decoration: new InputDecoration(hintText: 'Enter a custom path',),),
              new Text("File path of the record: ${_recording.path}"),
              new Text("Format: ${_recording.audioOutputFormat}"),
              new Text("Extension : ${_recording.extension}"),
              new Text("Audio recording duration : ${_recording.duration.toString()}" )
            ]),
      ),
    );
  }

  _start() async {
    try {
      if (await Pcm16khzAudioRecorder.hasPermissions) {
        if (_controller.text != null && _controller.text != "") {
          String path = _controller.text;
          if (!_controller.text.contains('/')) {
            Directory appDocDirectory = await getApplicationDocumentsDirectory();
            path = appDocDirectory.path + '/' + _controller.text;
          }
          print("Start recording: $path");
          await Pcm16khzAudioRecorder.start(path: path, audioOutputFormat: AudioOutputFormat.AAC);
        } else {
          await Pcm16khzAudioRecorder.start();
        }
        bool isRecording = await Pcm16khzAudioRecorder.isRecording;
        setState(() {
          _recording = new Recording(duration: new Duration(), path: "");
          _isRecording = isRecording;
        });
      } else {
        Scaffold.of(context).showSnackBar(new SnackBar(content: new Text("You must accept permissions")));
      }
    } catch (e){
      print(e);
    }
  }

  _stop() async {
    var recording = await Pcm16khzAudioRecorder.stop();
    print("Stop recording: ${recording.path}");
    bool isRecording = await Pcm16khzAudioRecorder.isRecording;
    File file = await new File(recording.path);
    print("  File length: ${await file.length()}");
    setState(() {
      _recording = recording;
      _isRecording = isRecording;
    });
    _controller.text = recording.path;
  }
}
