<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    
        
    
    <#if section = "title">
        ${msg("loginTitle",realm.name)}
    <#elseif section = "form">

        <style>

            /* Within keycloak themes folder.*/
        .container-div{
            position: relative; /* Establish a containing block for absolute positioning */
            width: 300px;
            height: 200px;
            background-color: lightblue;
            margin: 0 auto;
            margin-bottom:30px;
            margin-top:10px;
            border-radius: 25px;
            box-shadow: 0 4px 6px rgb(0 0 0 / 18%);
        }
        .foreground-div{
            position: absolute;
            top: 0px;
            left: 0px;
            width: 100%;
            height: 225px;
            z-index: 2;
            box-sizing: border-box;
            border-radius: 10px;
        }
        .videoOverlay{
            
        }
        .detectionParams{
            padding: 5px;
            margin: 5px;
        }
        .background-div{
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 225px;
        /*background-color: coral;*/
        z-index: 1; /* Lower z-index */
        }
        .videoDiv{
            border-radius: 25px;
        }
        .selfieImg{
            width:300px;
            z-index: 2;
        }













        /* For camera button overlay*/

        .videoOverlay{

        }



        .background-div {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 225px;
            /*background-color: coral;*/
            z-index: 1; /* Lower z-index */
        }

        .foreground-div {
            position: absolute;
            top: 0px;
            left: 0px;
            width: 100%;
            height: 225px;
            z-index: 2;
            box-sizing: border-box;
            border-radius: 20px;
        }
        .shutterBtn{
            margin: 0 auto;
            width: 100%;
            border: 2px solid #398656;
            height: auto;
            box-sizing: content-box;
            position: absolute;
            top: 0;
            bottom: 0;
            text-align: center;
            display: grid;
            place-items: center;
            color: rgb(63 221 49 / 0%);
            font-size: 28px;
            border-radius: 50%;
            width: 20%;
            height: 25%;
            top: 70%;
            left: 38%;
            background: radial-gradient(circle at 20% 20%, #70bc524f, #80b498);
            box-shadow: 0 10px 15px rgb(193 193 193 / 70%);
            text-shadow: 0px 0px 2px #1a571d57;
            transition: background-color 0.3s ease;
            display: none;
        }

        .shutterBtn:hover{
            font-size: 28px;
            box-shadow: 0 10px 15px rgb(193 193 193 / 49%);
            background: radial-gradient(circle at 20% 80%, #70bc524f, #90caab);
            border: 2px solid #61816d;
            width: 20%;
            height: 25%;
            top: 70%;
            left: 38%;
            text-shadow: rgba(0, 0, 0, 1.34) 0px 0px 2px;
            color: #58d34d00;
            cursor:pointer;
            transition: background-color 1s ease;
        }
        .shutter:active{
            text-shadow: #fa0f0f00;
            color: #fa0f0f00;
            cursor:pointer;
            transition: background-color 1s ease;
        }
        .closeBtn{
            float: right;
            border: 1px solid rgb(24, 77, 0);
            color:gray;
            font-size: 10px;
            align-content: center;
            align-items: center;
            text-align: center;
            padding: auto;
            vertical-align: middle;
            justify-content: center;
            display: flex;
            margin: 1px auto;

            width: 15px; /* Sphere width */
            height: 15px; /* Sphere height (same as width for a perfect circle) */
            background: radial-gradient(circle at 30% 30%, #a8ff91, #a83434);
            border-radius: 50%; /* Rounds the div into a circle */
            box-shadow: 0 15px 30px rgba(0, 0, 0, 0.4); /* Adds depth with shadow */
            margin-top: 3px;
            margin-right: -1px;
            display: none;
        }
        .closeBtn:hover{
            cursor: pointer;
            background: radial-gradient(circle at 20% 50%, #a8ff91, #a83434);
            box-shadow: 0 15px 30px rgba(255, 255, 255, 0.4); 
        }
        .hiddenDiv{
            visibility: hidden;
        }
        .visibleDiv{
            visibility: visible;
        }
        .videoDiv{
            border-radius: 25px;
        }
        .genderVal{
            color:green;
            text-shadow: 0px 0px 2px #eaffee;
        }
        .ageVal{
            color:green;
            text-shadow: 0px 0px 2px #eaffee;
        }
        .selfieAnalysis{
            color: green;
            text-shadow: 0px 0px 2px #eaffee;
        }
        
        </style>
        
        <link rel="stylesheet" href="${url.resourcesPath}/css/face-validation.css">
        
        <form id="kc-totp-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" enctype="multipart/form-data" method="post">
            
            <div class="${properties.kcFormGroupClass!}">

                <#--

                <div id='selfieAnalysis' class='container-div'>
                    <div class='foreground-div videoOverlay'>
                        <div class='detectionParams'>
                            <div>Confidence: 100%</div>
                            <div>Age, Gender: 37, M</div>
                            <div>
                                <span class='selfieOk'>Looks good!</span>
                                <span class='selfieNotOk'>Image should be clear</span>
                            </div>
                        </div>
                        <canvas width="300" id="detectionCanvasRef" style="position: absolute; top: 0; left: 0;"></canvas>
                    </div>
                    <div class='background-div'>
                        <img id='selfiePhoto' class="selfieImg videoDiv" style="display:block;"/>
                    </div>
                </div>
                
                -->





                
                <div id='cameraSection' class='container-div videoContent visibleDiv'>

                    <div class='foreground-div videoOverlay'>
                    
                        <div class='closeBtn' onClick=''>
                            <span class='fa fa-solid fa-close'></span>
                        </div>
                    

                        <div>
                            <div class='detectionParams'>
                                <div id='securityLevel'></div>
                                <div>Confidence: <span id='confidenceVal' class='confidenceVal'>100%</span></div>
                                <div>
                                    Age, Gender: <span id='ageVal' class='ageVal'>37</span>, <span id='genderVal' class='genderVal'>M</span>
                                </div>
                                <div>
                                    <span id='selfieAnalysis' class='selfieAnalysis'>Image should be clear</span>
                                </div>
                            </div>
                        </div>

                        <div id='takeScreenShot' class='shutterBtn'><span onClick={capturePhoto} class='fa fa-solid fa-camera'></span></div>
                    </div>
                    
                    <div class='background-div'>
                        <video id='videoelmt' class='videoDiv' playsInline style="width: 100%"></video>
                        <canvas  id='detectionCanvasRef' style="width:100%; position: absolute; top: 0; left: 0; pointerEvents: none; z-index: 3;"></canvas>
                        <img id='selfiePhoto' class="selfieImg videoDiv" style="display:none; width:100%;position: absolute; top: 0; left: 0; pointerEvents: none; z-index: 2;"/>
                    </div>
                     <input id='identityCustomerId' type="hidden" name="identityCustomerId" value="${identityCustomerId!''}" />
                     <input id='imageCanvas' type="hidden" name="imageCanvas" />


                <script>

                        const minimumConfidence = 0.7;

                        document.getElementById('securityLevel').innerHTML = minimumConfidence;

                        const calculateRotationAngle = (topLeft, topRight) => {
                            const deltaY = topRight.y - topLeft.y;
                            const deltaX = topRight.x - topLeft.x;

                            // Use atan2 to calculate the angle in radians
                            const angleInRadians = Math.atan2(deltaY, deltaX);

                            // Convert the angle to degrees (optional)
                            const angleInDegrees = (angleInRadians * 180) / Math.PI;

                            return {
                            radians: angleInRadians,
                            degrees: angleInDegrees,
                            };
                        };

                        async function createDotCustomer() {
                            try{
                                const PROVIDE_SELFIE_URL = "https://id-api.sozuri.net/api/v1/customers";
                                
                                // Headers to include in the request
                                const headers = {
                                    'Authorization': 'Bearer ', // Example: Authorization header
                                    'Content-Type': 'application/json',    // Ensure JSON content type
                                };

                                const response = await fetch(PROVIDE_SELFIE_URL, {
                                    method: 'POST', // *GET, POST, PUT, DELETE, etc.
                                    headers: headers,
                                });

                                if (!response.ok) {
                                    throw new Error("HTTP error! Status:"+ response.status);
                                }

                                const jsonResponse = await response.json();
                               
                                return jsonResponse.id;

                            }catch(Error){
                                console.error(Error);
                            }
                        }

                        async function postCustomerSelfie(base64Data, customerId) {
                            try{
                                const PROVIDE_SELFIE_URL = "https://id-api.sozuri.net/api/v1/customers/"+customerId+"/selfie";
                                const data = {
                                    image: {
                                        data: base64Data.split(',')[1]
                                    }
                                }
                                // Headers to include in the request
                                const headers = {
                                    'Authorization': 'Bearer ', // Example: Authorization header
                                    'Content-Type': 'application/json',    // Ensure JSON content type
                                };

                                const response = await fetch(PROVIDE_SELFIE_URL, {
                                    method: 'PUT', // *GET, POST, PUT, DELETE, etc.
                                    headers: headers,
                                    body: JSON.stringify(data), // body data type must match "Content-Type" header
                                });

                                if (!response.ok) {
                                    throw new Error("HTTP error! Status:"+ response.status);
                                }

                                const jsonResponse = await response.json();
                                
                                const detection = jsonResponse;

                                return detection;

                            }catch(Error){
                                console.error(Error);
                            }
                        }

                        var detectingFace = false;
                        var intervalId = '';
                        var faceDetected = false;
                        var detectedFaces = 0;
                        var numberOfFacesToDetect = 3;

                        var customerId = '';
                        
                        (async ()=>{customerId = await createDotCustomer()})();//document.getElementById('identityCustomerId').value;

                        async function getCustomer(customerId){
                            try{

                                const GET_CUSTOMER_URL = "https://id-api.sozuri.net/api/v1/customers/"+customerId;
                                
                                // Headers to include in the request
                                const headers = {
                                    'Authorization': 'Bearer ', // Example: Authorization header
                                    'Content-Type': 'application/json',    // Ensure JSON content type
                                };

                                const response = await fetch(GET_CUSTOMER_URL, {
                                    method: 'GET', // *GET, POST, PUT, DELETE, etc.
                                    headers: headers,
                                });

                                if (!response.ok) {
                                    throw new Error("HTTP error! Status:"+ response.status);
                                }

                                return await response.json();
                                
                            }catch(Error){
                                console.error(Error);
                            }
                        }       

                        function detectFace(){

                            console.log(' ::: detectingFace ::::: ',detectingFace);
                            clearInterval(intervalId);
                            
                            try{    
                                if(!detectingFace && faceDetected===false){
                                    detectingFace = true;
                                    const video = document.getElementById('videoelmt');
                                    
                                    if (video) {
                                       video.play();
                                       const highlightFace = async () => {
                                            const video = document.getElementById('videoelmt');
                                            const canvas_ = document.getElementById('detectionCanvasRef');
                                            if (!canvas_) 
                                                return;
                                            
                                            canvas_.width = video.videoWidth * 1;
                                            canvas_.height = video.videoHeight * 1;
                                            
                                            const ctx2 = canvas_.getContext('2d');
                                            ctx2.drawImage(video, 0, 0, canvas_.width, canvas_.height);
                                            
                                            const base64Str = canvas_.toDataURL('image/png');

                                            document.getElementById('imageCanvas').value = base64Str;
                                            
                                            ctx2.clearRect(0, 0, canvas_.width, canvas_.height);
                                            console.log({ customerId });
                                            //const customerId = await createDotCustomer();
                                            const detection = await postCustomerSelfie(base64Str, customerId);
                                            console.log({ detection });
                                            if(detection?.detection?.confidence && detection?.detection?.confidence>=minimumConfidence){

                                                document.getElementById('confidenceVal').innerHTML = (detection?.detection?.confidence*100).toFixed(1)+"%";
                                                
                                                const customer =  await getCustomer(customerId);
                                                console.log(' >>> customer?.customer?.age >>> ',customer?.customer?.age);
                                                if(customer?.customer?.age){
                                                    document.getElementById('ageVal').innerHTML = customer?.customer?.age?.selfie;
                                                    document.getElementById('genderVal').innerHTML = customer?.customer?.gender?.selfie;
                                                    faceDetected = true;
                                                    
                                                    video.pause();
                                                    document.getElementById('selfiePhoto').src = base64Str;
                                                    document.getElementById('selfiePhoto').style.display = 'block';
                                                    numberOfFacesToDetect = numberOfFacesToDetect + 1;
                                                    setTimeout((()=>{document.getElementById('kc-totp-login-form').submit()}), 500);
                                                }

                                                const faceRectangle = detection.detection.faceRectangle;
                                                const radius = 0;
                                                const {
                                                    topLeft,
                                                    topRight,
                                                    bottomRight,
                                                    bottomLeft
                                                } = faceRectangle;

                                                const angle = calculateRotationAngle(topLeft, topRight).radians;
                                                const centerX = (faceRectangle.topLeft.x + faceRectangle.topRight.x) / 2;
                                                const centerY = (faceRectangle.topLeft.y + faceRectangle.bottomLeft.y) / 2;

                                                ctx2.clearRect(0, 0, canvas_.width, canvas_.height);
                                                ctx2.translate(centerX, centerY);
                                                ctx2.rotate(angle);
                                                ctx2.translate(-centerX, -centerY);

                                                ctx2.beginPath();
                                                ctx2.moveTo(faceRectangle.topLeft.x + radius, faceRectangle.topLeft.y); // Start at top-left corner, offset by radius

                                                // Top edge
                                                ctx2.lineTo(faceRectangle.topRight.x - radius, faceRectangle.topRight.y);
                                                ctx2.quadraticCurveTo(faceRectangle.topRight.x, faceRectangle.topRight.y, faceRectangle.topRight.x, faceRectangle.topRight.y + radius);

                                                // Right edge
                                                ctx2.lineTo(faceRectangle.bottomRight.x, faceRectangle.bottomRight.y - radius);
                                                ctx2.quadraticCurveTo(faceRectangle.bottomRight.x, faceRectangle.bottomRight.y, faceRectangle.bottomRight.x - radius, faceRectangle.bottomRight.y);

                                                // Bottom edge
                                                ctx2.lineTo(faceRectangle.bottomLeft.x + radius, faceRectangle.bottomLeft.y);
                                                ctx2.quadraticCurveTo(faceRectangle.bottomLeft.x, faceRectangle.bottomLeft.y, faceRectangle.bottomLeft.x, faceRectangle.bottomLeft.y - radius);

                                                // Left edge
                                                ctx2.lineTo(faceRectangle.topLeft.x, faceRectangle.topLeft.y + radius);
                                                ctx2.quadraticCurveTo(faceRectangle.topLeft.x, faceRectangle.topLeft.y, faceRectangle.topLeft.x + radius, faceRectangle.topLeft.y);


                                                ctx2.closePath();
                                                // Style the overlay
                                                ctx2.strokeStyle = detection ?.detection?.confidence >= minimumConfidence ? "green" : "red"; // Outline color
                                                ctx2.lineWidth = 2; // Outline width

                                                ctx2.fillStyle = detection ?.detection?.confidence >= minimumConfidence ? "rgba(173, 216, 230, 0.3)" : "rgba(255, 0, 0, 0.3)"; // Fill color with transparency
                                                ctx2.fill();
                                                ctx2.stroke();

                                                //video.style.display = 'none';
                                                ///document.getElementById('videoelmt').style.display = 'none';
                                                
                                            }else{
                                                video.play();
                                                video.style.display = 'block';
                                                faceDetected = false;
                                            }

                                       }

                                       highlightFace();

                                    }
                                }
                            }catch(Error){
                                console.error(Error);
                            }finally{
                                detectingFace = false;
                                if(faceDetected===false){
                                    intervalId = setInterval(detectFace, 500);
                                }
                            }

                        }

                        intervalId = setInterval(detectFace, 500);


                        const constrains = {
                            video: {
                                width: {min:1, ideal: 360},
                                height: {min:1, ideal: 240}
                            }
                        };
                        const video = document.getElementById('videoelmt');

                        navigator.mediaDevices.getUserMedia(constrains)
                        .then((stream) => {video.srcObject =stream});
                            
                        const buttonTakeScreenShot = document.querySelector('#takeScreenShot');
                        const canvas = document.getElementById('canvas01');
                        const img = document.getElementById('capturedImg2');
                        const imgText = document.getElementById('imageCanvas');
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
                </script>
            
                <div class="${properties.kcFormGroupClass!}">
                    <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                        <div class="${properties.kcFormOptionsWrapperClass!}">
                        </div>
                    </div>
    
    
                    <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                        <div class="${properties.kcFormButtonsWrapperClass!}">
                            <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg('doSubmit')}"/>
                        </div>
                    </div>
                </div>
            </div>
        </form>
        <#if client?? && client.baseUrl?has_content>
            <p><a id="backToApplication" href="${client.baseUrl}">${msg("backToApplication")}</a></p>
        </#if>
    </#if>
</@layout.registrationLayout>