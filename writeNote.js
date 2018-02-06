import React, { Component, PropTypes } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Alert,
  TextInput,
  Image,
  Button,
  BackAndroid,
  Platform,
  StatusBar,
  Keyboard
} from 'react-native';
//var SQLite = require('react-native-sqlite-storage');
//SQLite.DEBUG(true);
//SQLite.enablePromise(false);
//const client = require('./client');
//const dbClient = require('./dbClient');
var moment = require('moment');
let listener=null;
let realm = require('./dbClient');

class writeNote extends Component {
  static propTypes = {
    title: PropTypes.string.isRequired,
    id: PropTypes.number.isRequired,
    navigator: PropTypes.object.isRequired
  }
  constructor(props,context) {
    super(props,context);
    this.state = {
      content: "",
      date: moment().format('MMM DD')
    };
    this.handleBackButton = this.handleBackButton.bind(this);
  }
  componentWillMount(){
    if(Platform.OS == "android"){
      listener = BackAndroid.addEventListener('hardwareBackPress',this.handleBackButton);
    }
    if(this.props.id > 0)
      this.loadNoteById(this.props.id);
  }
  componentWillUnmount(){
    //console.log("unmount");
    BackAndroid.removeEventListener('hardwareBackPress',this.handleBackButton);
  }
  handleBackButton = () => {
    if(this.state.content != ""){
      if(this.props.id > 0){
        this.updateNote(this.state.content,this.props.id);
      }
      else{
        this.saveNote(this.state.content);
      }
    }
    this.props.navigator.pop();
    Keyboard.emit('keyboardWillShow',null);
    return true;
  }
  updateNote(content,id){
    realm.write(() => {
      realm.create('Note',{id:id,content:content,updated:new Date()},true);
    });
  }
  saveNote(content){
    realm.write(() => {
      realm.create('Note',{id:realm.objects('Note').length + 1,content:content});
    });
  }
  loadNoteById(id){
    //let note = realm.objects('Note').filtered('id='+id);
    if(note.length > 0){
      this.setState({content: note[0].content,date: moment(new Date(note[0].created)).format("MMM DD")});
    }
  }
  render(){
    return (
      <View style={styles.pageStyle}>
        <Text style={styles.dateStyle}>
          {this.state.date}
        </Text>
        <TextInput
          style={styles.contentStyle}
          onChangeText={(text) => this.setState({content:text})}
          multiline={true}
          autoFocus={true}
          underlineColorAndroid='rgba(0,0,0,0)'>
          {this.state.content}
        </TextInput>
      </View>
    );
  }
}

const styles = StyleSheet.create({
    pageStyle: {
      flex: 1,
      flexDirection: 'column',
      backgroundColor: 'rgb(238,233,215)'
    },
    dateStyle:{
      textAlign: "center",
      fontWeight: '900',
      fontSize: 16,
      marginTop: 10,
      color: "rgb(63,43,37)"
    },
    backgroundImage: {
      position: 'absolute'

    },
    contentStyle: {
      fontSize: 14,
      marginLeft: 10,
      textAlignVertical: "top",
      flex: 0.8,
      color: "#000"
    }
});

module.exports = writeNote;