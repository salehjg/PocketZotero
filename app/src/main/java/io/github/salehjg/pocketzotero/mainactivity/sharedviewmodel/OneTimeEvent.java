package io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel;

public class OneTimeEvent {

    private Boolean received;

    public OneTimeEvent() {
        received = false;
    }

    public Boolean receive () {
        if (!received) {
            received = true;
            return true;
        }
        return false;
    }
}