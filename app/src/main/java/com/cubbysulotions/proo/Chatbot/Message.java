package com.cubbysulotions.proo.Chatbot;

public class Message {

  private String message;
  private boolean isReceived;
  private String text;

  public Message() {
  }

  public Message(String text) {
    this.text = text;
  }

  public Message(String message, boolean isReceived) {
    this.message = message;
    this.isReceived = isReceived;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean getIsReceived() {
    return isReceived;
  }

  public void setIsReceived(boolean isReceived) {
    this.isReceived = isReceived;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
