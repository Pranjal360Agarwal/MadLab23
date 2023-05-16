/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: TrackIndex.java
 *      Notes: Contains trackTableView indexing logic and data for MusicPlayerController.
 */

package com.iandw.musicplayerjavafx.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TrackIndex {
    // Holds each current track index in succession so that no track currently in TableView is played
    // twice while SHUFFLE is ON.
    private final List<Integer> shuffleArray = new ArrayList<>();

    // Holds each previous track so that user can play back previous tracks in succession.
    // Resets everytime TableView is refreshed.
    private final Stack<Integer> previousIndexStack = new Stack<>();
    private int currentTrackIndex;
    private int nextTrackIndex;
    private int tableSize;
    private boolean pushCurrentTrackToStack;


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                         ADD / PUSH / POP / PEEK / CLEAR
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void addToShuffleArray(int randomIndex) {
        shuffleArray.add(randomIndex);
    }

    public void pushToPreviousIndexArray(int currentTrackIndex) {
        if (pushCurrentTrackToStack) {
            previousIndexStack.push(currentTrackIndex);
        }
        
        pushCurrentTrackToStack = true;
    }

    public int popPreviousIndexArray() {
        return previousIndexStack.pop();
    }

    public int peekPreviousIndexArray() {
        if (!previousIndexStack.empty()) {
            return previousIndexStack.peek();
        }
        return currentTrackIndex;
    }

    public void clearShuffleArray() { shuffleArray.clear(); }
    public void clearPreviousIndexStack() {
        previousIndexStack.clear();
        pushCurrentTrackToStack = false;
    }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                         SETTERS / GETTERS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void setTableSize(int tableSize) { this.tableSize = tableSize; }
    public void setCurrentTrackIndex(int currentTrackIndex) { this.currentTrackIndex = currentTrackIndex; }
    public void setNextTrackIndex(int nextTrackIndex) { this.nextTrackIndex = nextTrackIndex; }
    public void setPushCurrentTrackToStack(boolean bool) { pushCurrentTrackToStack = bool; }


    public List<Integer> getShuffleArray() { return shuffleArray; }
    public Stack<Integer> getPreviousIndexStack() { return previousIndexStack; }
    public int getTableSize() { return tableSize; }
    public int getCurrentTrackIndex() { return currentTrackIndex; }
    public int getNextTrackIndex() { return nextTrackIndex; }
    public boolean getPushCurrentTrackToStack() { return pushCurrentTrackToStack; }

}
