import 'core-js';
import RainRenderer from "./rain-renderer";

import loadImages from "./image-loader";
import createCanvas from "./create-canvas";
import TweenLite from 'gsap';
import times from './times';
import {random,chance} from './random';

let textureRainFg, textureRainBg,
  textureStormLightningFg, textureStormLightningBg,
  textureFalloutFg, textureFalloutBg,
  textureSunFg, textureSunBg,
  textureDrizzleFg, textureDrizzleBg,
  dropColor, dropAlpha;

let textureFg,
  textureFgCtx,
  textureBg,
  textureBgCtx;

let textureBgSize={
  width:384,
  height:256
}
let textureFgSize={
  width:96,
  height:64
}

let renderer,
  canvas;

let parallax={x:0,y:0};

let weatherData=null;
let curWeatherData=null;
let blend={v:0};

function loadTextures(){
  loadImages([
    {name:"dropAlpha",src:"img/drop-alpha.png"},
    {name:"dropColor",src:"img/drop-color.png"},

    {name:"textureRainFg",src:"img/weather/texture-rain-fg.png"},
    {name:"textureRainBg",src:"img/weather/texture-rain-bg.png"},

    {name:"textureStormLightningFg",src:"img/weather/texture-storm-lightning-fg.png"},
    {name:"textureStormLightningBg",src:"img/weather/texture-storm-lightning-bg.png"},

    {name:"textureFalloutFg",src:"img/weather/texture-fallout-fg.png"},
    {name:"textureFalloutBg",src:"img/weather/texture-fallout-bg.png"},

    {name:"textureSunFg",src:"img/weather/texture-sun-fg.png"},
    {name:"textureSunBg",src:"img/weather/texture-sun-bg.png"},

    {name:"textureDrizzleFg",src:"img/weather/texture-drizzle-fg.png"},
    {name:"textureDrizzleBg",src:"img/weather/texture-drizzle-bg.png"},
  ]).then((images)=>{
    textureRainFg = images.textureRainFg.img;
    textureRainBg = images.textureRainBg.img;

    textureFalloutFg = images.textureFalloutFg.img;
    textureFalloutBg = images.textureFalloutBg.img;

    textureStormLightningFg = images.textureStormLightningFg.img;
    textureStormLightningBg = images.textureStormLightningBg.img;

    textureSunFg = images.textureSunFg.img;
    textureSunBg = images.textureSunBg.img;

    textureDrizzleFg = images.textureDrizzleFg.img;
    textureDrizzleBg = images.textureDrizzleBg.img;

    dropColor = images.dropColor.img;
    dropAlpha = images.dropAlpha.img;

    init();
  });
}
loadTextures();

function init(){
  canvas=document.querySelector('#container');

  let dpi=window.devicePixelRatio;
  canvas.width=window.innerWidth*dpi;
  canvas.height=window.innerHeight*dpi;
  canvas.style.width=window.innerWidth+"px";
  canvas.style.height=window.innerHeight+"px";

  textureFg = createCanvas(textureFgSize.width,textureFgSize.height);
  textureFgCtx = textureFg.getContext('2d');
  textureBg = createCanvas(textureBgSize.width,textureBgSize.height);
  textureBgCtx = textureBg.getContext('2d');

  generateTextures(textureRainFg,textureRainBg);

  renderer = new RainRenderer(canvas, raindrops.canvas, textureFg, textureBg, null,{
    brightness:1.04,
    alphaMultiply:6,
    alphaSubtract:3,
    // minRefraction:256,
    // maxRefraction:512
  });

  setupEvents();
}

function setupEvents(){

  setupParallax();
  setupWeather();
  setupFlash();
}
function setupParallax(){
  document.addEventListener('mousemove',(event)=>{
    let x=event.pageX;
    let y=event.pageY;

    TweenLite.to(parallax,1,{
      x:((x/canvas.width)*2)-1,
      y:((y/canvas.height)*2)-1,
      ease:Quint.easeOut,
      onUpdate:()=>{
        renderer.parallaxX=parallax.x;
        renderer.parallaxY=parallax.y;
      }
    })
  });
}
function setupFlash(){
  setInterval(()=>{
    if(chance(curWeatherData.flashChance)){
      flash(curWeatherData.bg,curWeatherData.fg,curWeatherData.flashBg,curWeatherData.flashFg);
    }
  },500)
}


function flash(baseBg,baseFg,flashBg,flashFg){
  let flashValue={v:0};
  function transitionFlash(to,t=0.025){
    return new Promise((resolve,reject)=>{
      TweenLite.to(flashValue,t,{
        v:to,
        ease:Quint.easeOut,
        onUpdate:()=>{
          generateTextures(baseFg,baseBg);
          generateTextures(flashFg,flashBg,flashValue.v);
          renderer.updateTextures();
        },
        onComplete:()=>{
          resolve();
        }
      });
    });
  }

  let lastFlash=transitionFlash(1);
  times(random(2,7),(i)=>{
    lastFlash=lastFlash.then(()=>{
      return transitionFlash(random(0.1,1))
    })
  })
  lastFlash=lastFlash.then(()=>{
    return transitionFlash(1,0.1);
  }).then(()=>{
    transitionFlash(0,0.25);
  });

}
