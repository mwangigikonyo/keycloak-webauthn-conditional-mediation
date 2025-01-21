

/*

console.log('face-validation.js');

function initVideo(){
    const constrains = {
        video: {
            width: {min:1, ideal: 360},
            height: {min:1, ideal: 240}
        }
    };

    const video = document.getElementById('videoelmt');

    console.log({  video });
    navigator.mediaDevices.getUserMedia(constrains)
    .then((stream) => {video.srcObject =stream});

}


const constrains = {
    video: {
        width: {min:1, ideal: 360},
        height: {min:1, ideal: 240}
    }
};
const video = document.getElementById('videoelmt');

console.log({  video });
navigator.mediaDevices.getUserMedia(constrains)
.then((stream) => {video.srcObject =stream});
 
const buttonTakeScreenShot = document.querySelector('#takeScreenShot');
const canvas = document.getElementById('canvas01');
const img = document.getElementById('capturedImg2');
console.log({ img });
const imgText = document.getElementById('imageCanvas');
console.log({imgText});
buttonTakeScreenShot.onclick = video.onclick =  function(){ 
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    canvas.getContext('2d').drawImage(video, 0, 0);
    const base64Str = canvas.toDataURL('image/png');
    console.log({ base64Str });
    img.src = canvas.toDataURL('image/jpg');
    console.log(img.src );
    imgText.value = img.src;
}





*/