package de.ricci.replicastudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlin.math.*

private val bg = Color(0xFF0C0D17)
private val panel = Color(0xFF191A2B)
private val purple = Color(0xFFB39AFF)
private data class V(val x:Float,val y:Float,val z:Float)
private data class Params(val shoulders:Float,val waist:Float,val hips:Float,val bust:Float,val height:Float)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme=darkColorScheme(primary=purple,background=bg,surface=panel)) { Studio() } }
    }
}
@Composable private fun Studio() {
    var reference by remember { mutableStateOf<android.net.Uri?>(null) }
    var target by remember { mutableStateOf<android.net.Uri?>(null) }
    val refPicker=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){reference=it}
    val targetPicker=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){target=it}
    var shoulders by remember { mutableFloatStateOf(1f) }
    var waist by remember { mutableFloatStateOf(1f) }
    var hips by remember { mutableFloatStateOf(1f) }
    var bust by remember { mutableFloatStateOf(1f) }
    var height by remember { mutableFloatStateOf(1f) }
    var yaw by remember { mutableFloatStateOf(.35f) }
    var pitch by remember { mutableFloatStateOf(.05f) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var clothed by remember { mutableStateOf(true) }
    var prompt by remember { mutableStateOf("Adjust the character's overall body proportions and pose while preserving facial identity, hair, lighting and background. Keep the result natural and anatomically consistent.") }
    val clipboard=LocalClipboardManager.current
    Column(Modifier.fillMaxSize().background(bg).verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        Text("◈ REPLICA STUDIO",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold,color=purple)
        Text("4.0 · Android Alpha 1 · Interactive 3D",color=Color.LightGray)
        Card(colors=CardDefaults.cardColors(containerColor=panel)) {
            Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                Text("01 · Bilder",fontWeight=FontWeight.Bold)
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f)) {
                        Button(onClick={refPicker.launch("image/*")},modifier=Modifier.fillMaxWidth()){Text("Referenz")}
                        if(reference!=null) AsyncImage(model=reference,contentDescription="Referenzfoto",modifier=Modifier.fillMaxWidth().height(145.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Button(onClick={targetPicker.launch("image/*")},modifier=Modifier.fillMaxWidth()){Text("Zielbild")}
                        if(target!=null) AsyncImage(model=target,contentDescription="Zielbild",modifier=Modifier.fillMaxWidth().height(145.dp))
                    }
                }
                Text("Bildvergleich und automatische Foto-zu-3D-Rekonstruktion folgen in späteren Versionen.",color=Color.LightGray,style=MaterialTheme.typography.bodySmall)
            }
        }
        Card(colors=CardDefaults.cardColors(containerColor=panel)) {
            Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text("02 · 3D Körper-Editor",fontWeight=FontWeight.Bold)
                Text("Mit dem Finger drehen · Zoom per Regler",style=MaterialTheme.typography.bodySmall,color=Color.LightGray)
                Avatar(Params(shoulders,waist,hips,bust,height),yaw,pitch,zoom,clothed,onOrbit={dx,dy->yaw+=dx*.012f;pitch=(pitch+dy*.007f).coerceIn(-.8f,.8f)})
                SliderRow("Schultern",shoulders){shoulders=it}
                SliderRow("Taille",waist){waist=it}
                SliderRow("Hüfte",hips){hips=it}
                SliderRow("Brustproportion",bust){bust=it}
                SliderRow("Körperlänge (relativ)",height){height=it}
                SliderRow("Zoom",zoom,.6f..1.8f){zoom=it}
                Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                    Text("Kleidungs-Silhouette",modifier=Modifier.weight(1f))
                    Switch(checked=clothed,onCheckedChange={clothed=it})
                }
                Text("Technischer parametrischer 3D-Drahtkörper – keine aus Fotos rekonstruierte Anatomie.",style=MaterialTheme.typography.bodySmall,color=Color.LightGray)
            }
        }
        Card(colors=CardDefaults.cardColors(containerColor=panel)) {
            Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                Text("03 · Secrets AI · Prompt",fontWeight=FontWeight.Bold)
                OutlinedTextField(value=prompt,onValueChange={prompt=it.take(500)},modifier=Modifier.fillMaxWidth(),minLines=4,label={Text("English correction prompt")})
                Text("${prompt.length}/500 Zeichen",color=if(prompt.length>=500)Color(0xFFFF9999) else purple)
                Button(onClick={clipboard.setText(AnnotatedString(prompt))},enabled=prompt.isNotBlank()){Text("Prompt kopieren")}
                Text("Manuell editierbar. Automatische KI-Prompts sind noch nicht aktiv.",style=MaterialTheme.typography.bodySmall,color=Color.LightGray)
            }
        }
    }
}
@Composable private fun SliderRow(label:String,value:Float,range:ClosedFloatingPointRange<Float>=.65f..1.35f,onChange:(Float)->Unit) {
    Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
        Text(label,modifier=Modifier.width(140.dp),style=MaterialTheme.typography.bodySmall)
        Slider(value=value,onValueChange=onChange,valueRange=range,modifier=Modifier.weight(1f))
        Text(String.format(java.util.Locale.US,"%.2f",value),modifier=Modifier.width(40.dp),style=MaterialTheme.typography.labelSmall)
    }
}
@Composable private fun Avatar(p:Params,yaw:Float,pitch:Float,zoom:Float,clothed:Boolean,onOrbit:(Float,Float)->Unit) {
    Canvas(Modifier.fillMaxWidth().height(360.dp).background(Color(0xFF101224)).border(1.dp,Color(0xFF3B3457)).pointerInput(Unit){detectDragGestures { change,drag -> change.consume();onOrbit(drag.x,drag.y) }}) {
        val cy=cos(yaw);val sy=sin(yaw);val cp=cos(pitch);val sp=sin(pitch)
        fun project(v:V):Offset {
            val x=v.x*cy+v.z*sy
            val z=-v.x*sy+v.z*cy
            val y=v.y*cp-z*sp
            val depth=y*sp+z*cp
            val scale=(size.minDimension*.31f*zoom)/(1f+depth*.12f)
            return Offset(size.width/2+x*scale,size.height*.54f-y*scale)
        }
        fun line(a:V,b:V,c:Color=purple,w:Float=2f){drawLine(c,project(a),project(b),w,cap=StrokeCap.Round)}
        fun ring(y:Float,rx:Float,rz:Float,color:Color=purple) {
            val n=24
            for(i in 0 until n) {
                val a=2*PI*i/n;val b=2*PI*(i+1)/n
                line(V((cos(a)*rx).toFloat(),y,(sin(a)*rz).toFloat()),V((cos(b)*rx).toFloat(),y,(sin(b)*rz).toFloat()),color,1.8f)
            }
        }
        val h=p.height
        val hip=.45f*p.hips;val waistR=.33f*p.waist;val shoulder=.62f*p.shoulders
        val body=if(clothed) Color(0xFF88D9E8) else Color(0xFFB39AFF)
        val levels=listOf(0f to hip,.36f*h to hip,.88f*h to waistR,1.36f*h to (.44f*p.bust),1.72f*h to shoulder)
        for((y,r) in levels)ring(y,r,r*.57f,body)
        for(i in 0 until levels.lastIndex) {
            val (y1,r1)=levels[i];val (y2,r2)=levels[i+1]
            for(k in 0 until 12){val a=2*PI*k/12;line(V((cos(a)*r1).toFloat(),y1,(sin(a)*r1*.57f).toFloat()),V((cos(a)*r2).toFloat(),y2,(sin(a)*r2*.57f).toFloat()),body,1.6f)}
        }
        // Head, neck, arms and legs: all rotate in 3D with the torso.
        ring(1.85f*h,.17f,.15f)
        for(i in 0..6) {val y=1.94f*h+i*.085f*h;val r=.22f*sqrt(max(0f,1f-((i-3f)/4f).pow(2)));ring(y,r,r*.85f)}
        for(side in listOf(-1f,1f)) {
            val sx=side*shoulder
            line(V(sx,1.70f*h,0f),V(side*(shoulder+.18f),1.02f*h,.05f),body,5f)
            line(V(side*(shoulder+.18f),1.02f*h,.05f),V(side*(shoulder+.16f),.38f*h,.12f),body,4f)
            val lx=side*hip*.64f
            line(V(lx,.1f*h,0f),V(side*.32f,-.82f*h,.05f),body,8f)
            line(V(side*.32f,-.82f*h,.05f),V(side*.32f,-1.68f*h,.15f),body,6f)
            line(V(side*.32f,-1.68f*h,.15f),V(side*.32f,-1.70f*h,.38f),body,5f)
        }
    }
}
