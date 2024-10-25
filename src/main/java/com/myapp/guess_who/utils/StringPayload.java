package com.myapp.guess_who.utils;

// Every payload is serialized to JSON before sending via STOMP, so simple string arrives as "string" with added quotes.
// This serialization is required for things to be parsed properly, for example: Enum.
// Wrapping the String in an object gives access to the string without added quotes.
public record StringPayload(String payload) {

}
