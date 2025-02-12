<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "head">
        <link rel="stylesheet" href="${url.resourcesPath}/css/face-validation.css">
        <link rel="stylesheet" href="/css/face-validation.css">
    </#if>
    <#if section = "title">
        ${msg("loginTitle",realm.name)}
    <#elseif section = "form">
        <form id="kc-totp-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" enctype="multipart/form-data" method="post">
            <div class="${properties.kcFormGroupClass!}">
                  <div class='container-div'>
                  	<div class='foreground-div videoOverlay'>
                  		<div class='detectionParams'>
                  		</div>
                  		<canvas id="faceOverlayCanvas" width="300" style="position: absolute;  top: 0px; left: 0px; pointer-events: none;"></canvas>
                  	</div>
                  	<div class="background-div">
                  		<img id="capturedImg" class="selfieImg videoDiv" src="" alt="Captured">
                  	</div>
 
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="totp" class="${properties.kcLabelClass!}">Capture your photo and submit</label>
                    </div>
                    <br>
                    <br>
                    
                    
                    <div class="container-div videoContent">
                     
                      <div className='foreground-div videoOverlay'>
                      		<div className='shutterBtn'><span onClick={capturePhoto} className='fa fa-solid fa-camera'></span></div>
		                    <canvas id='canvas01'  class="${properties.kcInputClass!}" style="display:none;"></canvas>
		                    <br>
		                    <br>
		                    <input id="imageCanvas" name="imageCanvas" type="text" hidden/>
		                    <div style="width:100%; text-align: center;">            
		                        <span class="btn" style="margin:20px" id="takeScreenShot">Capture</span>
		                        <img id='capturedImg2' style="width:25%"></img>
		                    </div>
		                    <div className='background-div'>
		                    	<video id='videoelmt' style="width:100%" autoplay></video>
		                    </div>
		              </div>
		            </div>
                    <script>
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
                            console.log({ img })
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
                             
 
                    </script>
                  </div>
 
            </div>
 
            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                    </div>
                </div>
 
 
                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <div class="${properties.kcFormButtonsWrapperClass!}">
                        <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg("doSubmit")}"/>
                 </div>
            </div>
        </form>
        <#if client?? && client.baseUrl?has_content>
            <p><a id="backToApplication" href="${client.baseUrl}">${msg("backToApplication")}</a></p>
        </#if>
    </#if>
</@layout.registrationLayout>