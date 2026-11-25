package com.jarvis.android

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import java.util.Locale

private val Ink=Color(0xFF080D13)
private val Panel=Color(0xFF111B25)
private val Cyan=Color(0xFF00D9FF)
private val Mint=Color(0xFF42E6A4)

class MainActivity:ComponentActivity(),TextToSpeech.OnInitListener {
    private var tts:TextToSpeech?=null
    private val micPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchRecognition() else speak("Microphone permission is needed for voice input.")
    }
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        tts=TextToSpeech(this,this)
        setContent { JarvisApp(onSpeak={speak(it)},onMic={startVoice()}) }
    }
    override fun onInit(status:Int){ if(status==TextToSpeech.SUCCESS) tts?.language=Locale("en","IN") }
    private fun speak(text:String){ tts?.speak(text,TextToSpeech.QUEUE_FLUSH,null,"jarvis") }
    private fun startVoice(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            launchRecognition()
        } else {
            micPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
    private fun launchRecognition(){
        if(!SpeechRecognizer.isRecognitionAvailable(this)) {
            speak("Speech recognition is not available on this device.")
            return
        }
        val i=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak to JARVIS")
        }
        runCatching { startActivityForResult(i,77) }
            .onFailure { speak("Could not start voice input. Please try again.") }
    }
    @Deprecated("Legacy speech activity result")
    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){
        super.onActivityResult(requestCode,resultCode,data)
        if(requestCode==77 && resultCode==RESULT_OK){
            val phrase=data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            if(phrase.isNotBlank()) speak("You said: $phrase. You can send this in chat.")
        }
    }
    override fun onDestroy(){ tts?.shutdown(); super.onDestroy() }
}

@Composable
private fun JarvisApp(onSpeak:(String)->Unit,onMic:()->Unit){
    var tab by remember { mutableStateOf("Home") }
    var prompt by remember { mutableStateOf("") }
    var api by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("Your assistant is ready. Configure your AI provider in Settings.") }
    var busy by remember { mutableStateOf(false) }
    val context= LocalContext.current
    val storage = remember(context) {
        context.getSharedPreferences("jarvis_local_data", android.content.Context.MODE_PRIVATE)
    }
    val scope= rememberCoroutineScope()
    val history= remember(storage) {
        mutableStateListOf<String>().apply {
            addAll(readStoredList(storage, "history"))
            if (isEmpty()) add("JARVIS initialized")
        }
    }
    val memories= remember(storage) {
        mutableStateListOf<String>().apply { addAll(readStoredList(storage, "memories")) }
    }
    val tasks= remember(storage) {
        mutableStateListOf<String>().apply { addAll(readStoredList(storage, "tasks")) }
    }
    MaterialTheme(colorScheme=darkColorScheme(primary=Cyan,secondary=Mint,background=Ink,surface=Panel)){
        Scaffold(containerColor=Ink,bottomBar={
            NavigationBar(containerColor=Panel){
                listOf("Home","Agents","Activity","Settings").forEach { name ->
                    NavigationBarItem(selected=tab==name,onClick={tab=name},icon={
                        Icon(when(name){"Home"->Icons.Default.Home;"Agents"->Icons.Default.Hub;"Activity"->Icons.Default.History;else->Icons.Default.Settings},null)
                    },label={Text(name)},colors=NavigationBarItemDefaults.colors(selectedIconColor=Cyan,indicatorColor=Cyan.copy(.12f),selectedTextColor=Cyan,unselectedTextColor=Color.LightGray))
                }
            }
        }) { pad ->
            Column(Modifier.fillMaxSize().padding(pad).padding(horizontal=18.dp)){
                Row(Modifier.fillMaxWidth().padding(top=16.dp,bottom=18.dp),verticalAlignment=Alignment.CenterVertically){
                    Column(Modifier.weight(1f)){
                        Text("J A R V I S",color=Cyan,fontSize=20.sp,fontWeight=FontWeight.Black,letterSpacing=3.sp)
                        Text("PERSONAL AI SYSTEM  •  ANDROID",color=Color.Gray,fontSize=9.sp,letterSpacing=1.4.sp)
                    }
                    Surface(color=Mint.copy(.12f),shape=CircleShape){ Text("● READY",Modifier.padding(horizontal=12.dp,vertical=7.dp),color=Mint,fontSize=10.sp,fontWeight=FontWeight.Bold) }
                }
                when(tab){
                    "Home" -> {
                        LazyColumn(verticalArrangement=Arrangement.spacedBy(13.dp),modifier=Modifier.weight(1f)){
                            item {
                                Box(Modifier.fillMaxWidth().height(220.dp).background(Brush.radialGradient(listOf(Cyan.copy(.22f),Ink)),RoundedCornerShape(28.dp)).clickable { onMic() },contentAlignment=Alignment.Center){
                                    Column(horizontalAlignment=Alignment.CenterHorizontally){
                                        Box(Modifier.size(124.dp).background(Brush.radialGradient(listOf(Cyan.copy(.38f),Mint.copy(.12f),Panel)),CircleShape),contentAlignment=Alignment.Center){
                                            Icon(Icons.Default.GraphicEq,null,tint=Cyan,modifier=Modifier.size(58.dp))
                                        }
                                        Spacer(Modifier.height(14.dp)); Text("TAP TO TALK",color=Cyan,fontWeight=FontWeight.Bold,letterSpacing=2.sp)
                                        Text("Voice assistant • Push to talk",color=Color.LightGray,fontSize=11.sp)
                                    }
                                }
                            }
                            item { Text("Good day. What shall we do?",fontSize=22.sp,fontWeight=FontWeight.Bold,color=Color.White) }
                            item { Row(horizontalArrangement=Arrangement.spacedBy(9.dp)){
                                Quick("Phone control",Icons.Default.PhoneAndroid){tab="Agents"}
                                Quick("Research",Icons.Default.Search){openWeb(context,"AI news today")}
                                Quick("Tasks",Icons.Default.TaskAlt){tab="Activity"}
                            } }
                            item { CardBlock("AI CONVERSATION"){
                                Text(answer,color=Color.LightGray,fontSize=13.sp)
                                Spacer(Modifier.height(10.dp))
                                OutlinedTextField(prompt,{prompt=it},Modifier.fillMaxWidth(),placeholder={Text("Ask JARVIS anything…")},maxLines=3)
                                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End,verticalAlignment=Alignment.CenterVertically){
                                    IconButton(onClick=onMic){Icon(Icons.Default.Mic,null,tint=Cyan)}
                                    Button(enabled=prompt.isNotBlank()&&!busy,onClick={
                                        val q=prompt.trim(); prompt=""; busy=true; answer="Thinking…"
                                        scope.launch {
                                            answer=if(api.isBlank()) localReply(q,context) else OpenRouter.ask(api,q,history.takeLast(12))
                                            history.add("You: $q")
                                            history.add("JARVIS: $answer")
                                            saveStoredList(storage, "history", history.takeLast(100))
                                            busy=false
                                        }
                                    }){Text(if(busy)"Working" else "Send")}
                                }
                            } }
                            item { CardBlock("QUICK STATUS"){
                                Text("AI provider: ${if(api.isBlank()) "Not configured" else "API key added"}",color=Color.LightGray,fontSize=12.sp)
                                Text("Memory entries: ${memories.size}   •   Tasks: ${tasks.size}",color=Color.LightGray,fontSize=12.sp)
                            } }
                        }
                    }
                    "Agents" -> LazyColumn(verticalArrangement=Arrangement.spacedBy(12.dp)){
                        item { Text("AGENT CONTROL",color=Cyan,fontWeight=FontWeight.Bold,letterSpacing=2.sp) }
                        item { AgentCard("Phone Assistant","Open apps, dialer, settings and user-approved screen actions","Available") { tab="Settings" } }
                        item { AgentCard("Research Agent","Search the web using your browser","Available") { openWeb(context,"") } }
                        item { AgentCard("Memory Agent","Save and manage local notes","Available") { tab="Activity" } }
                        item { AgentCard("Call Assistant","Launch dialer; call audio access depends on Android/carrier","Limited") { context.startActivity(Intent(Intent.ACTION_DIAL)) } }
                        item { AgentCard("Notification Agent","Optional notification listener permission","Setup required") { context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) } }
                        item { AgentCard("Screen Agent","Optional accessibility-based visible screen actions","Setup required") { context.startActivity(Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)) } }
                        item { AgentCard("TV / Smart Home","No device integration configured","Not connected") { } }
                    }
                    "Activity" -> LazyColumn(verticalArrangement=Arrangement.spacedBy(12.dp)){
                        item { Text("YOUR WORKSPACE",color=Cyan,fontWeight=FontWeight.Bold,letterSpacing=2.sp) }
                        item { CardBlock("LOCAL TASKS"){
                            OutlinedTextField(prompt,{prompt=it},Modifier.fillMaxWidth(),placeholder={Text("Add a reminder or task")})
                            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick={
                                        if(prompt.isNotBlank()){
                                            tasks.add(prompt)
                                            saveStoredList(storage, "tasks", tasks)
                                            prompt=""
                                        }
                                    },
                                    enabled=prompt.isNotBlank(),
                                    modifier=Modifier.weight(1f)
                                ){Text("Add task")}
                                OutlinedButton(
                                    onClick={
                                        val reminderText=prompt.trim()
                                        if(reminderText.isNotBlank()){
                                            if(scheduleReminder(context, reminderText)){
                                                val label="Reminder in 10 min: $reminderText"
                                                tasks.add(label)
                                                saveStoredList(storage, "tasks", tasks)
                                                prompt=""
                                            }
                                        }
                                    },
                                    enabled=prompt.isNotBlank(),
                                    modifier=Modifier.weight(1f)
                                ){Text("Remind in 10m")}
                            }
                            tasks.forEach { Text("• $it",color=Color.White,modifier=Modifier.padding(vertical=4.dp)) }
                        } }
                        item { CardBlock("MEMORY CENTER"){
                            OutlinedTextField(prompt,{prompt=it},Modifier.fillMaxWidth(),placeholder={Text("Save a memory")})
                            Button(onClick={
                                if(prompt.isNotBlank()){
                                    memories.add(prompt)
                                    saveStoredList(storage, "memories", memories)
                                    prompt=""
                                }
                            },modifier=Modifier.align(Alignment.End)){Text("Save")}
                            memories.forEach { m -> Row(verticalAlignment=Alignment.CenterVertically){
                                Text(m,Modifier.weight(1f),color=Color.White,fontSize=13.sp)
                                IconButton(onClick={
                                    memories.remove(m)
                                    saveStoredList(storage, "memories", memories)
                                }){Icon(Icons.Default.Delete,null,tint=Color.Gray)}
                            } }
                        } }
                        item { CardBlock("RECENT ACTIVITY"){
                            history.takeLast(12).reversed().forEach { Text(it,color=Color.LightGray,fontSize=12.sp,modifier=Modifier.padding(vertical=3.dp)) }
                        } }
                    }
                    else -> LazyColumn(verticalArrangement=Arrangement.spacedBy(12.dp)){
                        item { Text("SYSTEM SETTINGS",color=Cyan,fontWeight=FontWeight.Bold,letterSpacing=2.sp) }
                        item { CardBlock("AI PROVIDER • OPENROUTER"){
                            Text("Your API key stays on this device for this session. Add it to connect the assistant.",color=Color.LightGray,fontSize=12.sp)
                            OutlinedTextField(api,{api=it},Modifier.fillMaxWidth(),placeholder={Text("Paste OpenRouter API key")},singleLine=true)
                            Text("Do not share your API key. Cloud prompts are sent to your chosen model provider.",color=Color.Gray,fontSize=10.sp)
                        } }
                        item { AgentCard("Accessibility permission","Enable only if you want JARVIS to read visible screen text and perform user-requested actions.","Optional") { context.startActivity(Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)) } }
                        item { AgentCard("Notification access","Allow JARVIS to show notification summaries locally.","Optional") { context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) } }
                        item { AgentCard("App permissions","Microphone permission is requested when using voice input.","Review") { context.startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))) } }
                        item { AgentCard("Open Android settings","Manage permissions and system access yourself.","Open") { context.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)) } }
                        item { Text("JARVIS 4.0 • Preview build • No root required",color=Color.Gray,fontSize=10.sp,modifier=Modifier.padding(12.dp)) }
                    }
                }
            }
        }
    }
}

@Composable private fun RowScope.Quick(label:String,icon:androidx.compose.ui.graphics.vector.ImageVector,onClick:()->Unit){
    Card(Modifier.weight(1f).clickable(onClick=onClick),colors=CardDefaults.cardColors(containerColor=Panel),shape=RoundedCornerShape(16.dp)){
        Column(Modifier.fillMaxWidth().padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally){
            Icon(icon,null,tint=Cyan); Spacer(Modifier.height(6.dp)); Text(label,color=Color.White,fontSize=10.sp)
        }
    }
}
@Composable private fun CardBlock(title:String,content:@Composable ColumnScope.()->Unit){
    Card(colors=CardDefaults.cardColors(containerColor=Panel),shape=RoundedCornerShape(20.dp)){
        Column(Modifier.fillMaxWidth().padding(16.dp),content=content)
    }
}
@Composable private fun AgentCard(title:String,desc:String,status:String,onClick:()->Unit){
    Card(Modifier.fillMaxWidth().clickable(onClick=onClick),colors=CardDefaults.cardColors(containerColor=Panel),shape=RoundedCornerShape(18.dp)){
        Row(Modifier.padding(15.dp),verticalAlignment=Alignment.CenterVertically){
            Icon(Icons.Default.Bolt,null,tint=Cyan,modifier=Modifier.size(26.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)){Text(title,color=Color.White,fontWeight=FontWeight.SemiBold);Text(desc,color=Color.LightGray,fontSize=11.sp)}
            Text(status,color=Mint,fontSize=9.sp)
        }
    }
}
private fun readStoredList(preferences: SharedPreferences, key: String): List<String> = runCatching {
    val array = org.json.JSONArray(preferences.getString(key, "[]") ?: "[]")
    buildList {
        for (index in 0 until array.length()) {
            array.optString(index).takeIf { it.isNotBlank() }?.let { add(it) }
        }
    }
}.getOrDefault(emptyList())

private fun saveStoredList(preferences: SharedPreferences, key: String, values: List<String>) {
    val array = org.json.JSONArray()
    values.forEach { array.put(it) }
    preferences.edit().putString(key, array.toString()).apply()
}

private fun scheduleReminder(
    context: android.content.Context,
    text: String,
    delayMillis: Long = 10 * 60 * 1000L
): Boolean {
    val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
    val reminderIntent = Intent(context, ReminderReceiver::class.java).putExtra("text", text)
    val requestCode = (System.currentTimeMillis() and 0x7fffffffL).toInt()
    val pendingIntent = android.app.PendingIntent.getBroadcast(
        context,
        requestCode,
        reminderIntent,
        android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
    )
    return runCatching {
        alarmManager.setAndAllowWhileIdle(
            android.app.AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + delayMillis,
            pendingIntent
        )
        true
    }.getOrDefault(false)
}

private fun openWeb(context:android.content.Context,q:String){
    val url="https://www.google.com/search?q="+Uri.encode(q)
    context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(url)))
}
private suspend fun localReply(q:String,context:android.content.Context):String {
    val s=q.lowercase(Locale.ROOT)
    return when {
        s.contains("time") -> "The current time is ${java.text.SimpleDateFormat("h:mm a",Locale.getDefault()).format(java.util.Date())}."
        s.contains("date") -> "Today is ${java.text.SimpleDateFormat("EEEE, d MMMM yyyy",Locale.getDefault()).format(java.util.Date())}."
        s.contains("youtube") -> { context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://youtube.com"))); "Opening YouTube." }
        s.contains("whatsapp") -> { runCatching{context.startActivity(context.packageManager.getLaunchIntentForPackage("com.whatsapp"))}; "Opening WhatsApp if installed." }
        s.contains("instagram") -> { runCatching{context.startActivity(context.packageManager.getLaunchIntentForPackage("com.instagram.android"))}; "Opening Instagram if installed." }
        s.contains("settings") -> { context.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)); "Opening Android Settings." }
        s.contains("call") -> { context.startActivity(Intent(Intent.ACTION_DIAL)); "Opening the dialer. You choose and place the call." }
        s.contains("search") || s.contains("google") -> { openWeb(context,q); "Opening web search." }
        else -> "AI provider is not configured yet. Add your OpenRouter key in Settings, or ask me to open an app, settings, dialer, or web search."
    }
}
