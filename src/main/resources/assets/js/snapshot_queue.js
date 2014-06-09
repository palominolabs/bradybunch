BradyBunchRails.SnapshotQueue = function() {
    this._snapshots = [];
    this.maxSize = 10;

    this.add = function(snapshot) {
        this._snapshots.unshift(snapshot);

        while (this._snapshots.length > this.maxSize) {
            this._snapshots.pop();
        }
    };

    this.get = function(index) {
        if (index > this.maxSize) {
            index = this.maxSize - 1;
        }

        return this._snapshots[index];
    };

    this.currentLength = function () {
        return this._snapshots.length;
    };

    this.every = function (callback) {
        return this._snapshots.every(callback);
    }

    this.isFull = function () {
        return this._snapshots.length == this.maxSize;
    }
};
