
function refreshProgress() {
    UploadMonitor.getUploadInfo(updateProgress);
}

function refreshDSProgress() {
    DSMonitor.getDSInfo(updateDSProgress);
}

var progressPercent = 0;
function updateProgress(uploadInfo) {
    if (uploadInfo.inProgress) {
        var fileIndex = uploadInfo.fileIndex;
        progressPercent = Math.ceil((uploadInfo.bytesRead / uploadInfo.totalSize) * 100);
        document.getElementById('progressBarText').innerHTML = dwrUploadTransferMessage + progressPercent + '%';
        document.getElementById('progressBarBoxContent').style.width = parseInt(progressPercent * 3.5) + 'px';
        window.setTimeout('refreshProgress()', 100);
    } else if (uploadInfo.status == "done") {
        progressPercent = 100;
        document.getElementById('progressBarText').innerHTML = dwrUploadCompletedMessage;
        document.getElementById('progressBarBoxContent').style.width = parseInt(progressPercent * 3.5) + 'px';
        startDSProgress();
    }
    else {
        progressPercent = 0;
        document.getElementById('progressBarText').innerHTML = dwrUploadPrepareMessage + progressPercent + '%';
        document.getElementById('progressBarBoxContent').style.width = parseInt(progressPercent * 3.5) + 'px';
    }

    return true;
}


function updateDSProgress(dsInfo) {
    if (dsInfo.inProgress) {
        var progressPercentDS = Math.ceil((dsInfo.docsAdded / dsInfo.totalDocs) * 100);
        document.getElementById('progressDSBarText').innerHTML = dwrPopulationProcessingMessage + progressPercentDS + '%';
        document.getElementById('progressDSBarBoxContent').style.width = parseInt(progressPercentDS * 3.5) + 'px';
        window.setTimeout('refreshDSProgress()', 100);
    } else if (dsInfo.status == "done") {
        var progressPercentDS = 100;
        document.getElementById('progressDSBarText').innerHTML = dwrPopulationCompletedMessage;
        document.getElementById('progressDSBarBoxContent').style.width = parseInt(progressPercentDS * 3.5) + 'px';

    }
     else {
        var progressPercentDS = 0;
        document.getElementById('progressDSBarText').innerHTML = dwrPopulationPrepareMessage + progressPercentDS + '%';
        document.getElementById('progressDSBarBoxContent').style.width = parseInt(progressPercentDS * 3.5) + 'px';
    }

    return true;
}


function startProgress() {
    //updateStatusMessage("");
    document.getElementById('progressBar').style.display = 'block';
    document.getElementById('progressBarText').innerHTML = dwrUploadTransferMessage + '0%';
    /*
    if(document.getElementById('uploadbutton')) {
      document.getElementById('uploadbutton').disabled = true;
    } else {
      alert("button with id 'uploadbutton' missing")
    }
    */
    // wait a little while to make sure the upload has started ..
    window.setTimeout("refreshProgress()", 100);
    return true;
}

function startDSProgress() {
    //updateStatusMessage("");
    document.getElementById('progressDSBar').style.display = 'block';
    document.getElementById('progressDSBarText').innerHTML = dwrPopulationProcessingMessage + '0%';
    /*
    if(document.getElementById('uploadbutton')) {
      document.getElementById('uploadbutton').disabled = true;
    } else {
      alert("button with id 'uploadbutton' missing")
    }
    */
    // wait a little while to make sure the upload has started ..

    window.setTimeout("refreshDSProgress()", 1000);
    return true;
}



function hideProgressBar() {
    document.getElementById('progressBar').style.display = 'none';
    document.getElementById('progressBarText').innerHTML = '';
}

/* Removed since it caused problems
function UploadMonitor() {
    UploadMonitor._path = '';
    UploadMonitor.getUploadInfo = function(callback) {
        DWREngine._execute(UploadMonitor._path, 'UploadMonitor', 'getUploadInfo', callback);
    }
}
*/

/*
A helper function to present an ajax activity indicator while files are being uploaded.
Example usage: <span id="progressMsg" style="display:none; margin-left: 3px;"><img id="uploadindicator" src="<c:url value='/images/ajax/indicator.gif'/>" alt="Indicator" /></span>
Inspired by: http://www.gen-x-design.com/archives/ajax-activity-indicators-make-them-global-and-unobtrusive/1/
*/
function showProgressAnimation() {
    if ($('progressMsg')) {
        Effect.Appear('progressMsg', {duration: 0.25, queue: 'end'});
    }
    /*
    Element.show('progressMsg');
    var img = document.getElementById('uploadindicator');
    var imgName = img.src.toUpperCase();
    if (imgName.substring(imgName.length-3, imgName.length) == "GIF"){
      img.src = img.src;
    }*/
    //Effect.Fade('progressMsg');
}


function showDSProgressAnimation() {
    if ($('progressMsg')) {
        Effect.Appear('progressMsg', {duration: 0.25, queue: 'end'});
    }
    /*
    Element.show('progressMsg');
    var img = document.getElementById('uploadindicator');
    var imgName = img.src.toUpperCase();
    if (imgName.substring(imgName.length-3, imgName.length) == "GIF"){
      img.src = img.src;
    }*/
    //Effect.Fade('progressMsg');
}
