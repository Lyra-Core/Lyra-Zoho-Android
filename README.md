
# Enhanced Lyra-Zoho SDK

**Comprehensive SDK for integrating Lyra-Zoho services into your Android or iOS app.**

*Now with full chat functionality powered by Zoho SalesIQ!*

## Features

✅ **Basic SDK Initialization** - API key management and configuration  
🆕 **Full Chat Integration** - Complete Zoho SalesIQ chat functionality  
🆕 **Event Listeners** - Real-time chat event handling  
🆕 **Department Management** - Get and set chat departments  
🆕 **Visitor Management** - Set questions, language, and additional information  
🆕 **Session Management** - Start, monitor, and end chat sessions  

## Installation

### Android (Gradle via JitPack)

Add JitPack to your repositories and include the SDK as a dependency:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
    // Zoho SDK repository
    maven {
        url 'https://maven.zohodl.com'
        content {
            includeGroup "com.zoho.salesiq"
        }
    }
}

dependencies {
    implementation "com.github.Lyra-Core:Lyra-Zoho:1.0.0"
}
```

### iOS (CocoaPods)

```ruby
pod 'LyraZoho', :git => 'https://github.com/Lyra-Core/Lyra-Zoho.git', :tag => '1.0.0'
```

### iOS (Swift Package Manager)

Add the package in Xcode or in your `Package.swift`:

```swift
.package(url: "https://github.com/Lyra-Core/Lyra-Zoho.git", exact: "1.0.0")
```

## Usage

### Android (Kotlin)

#### Basic Setup in Application Class

```kotlin
import com.lyracore.zoho.LyraZoho
import com.lyracore.zoho.core.LyraConfig

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize basic SDK
        val config = LyraConfig(
            apiKey = "your-api-key",
            baseUrl = "https://api.zoho.com",
            timeoutMs = 15000
        )
        LyraZoho.initialize(config)
        
        // Initialize Chat functionality
        LyraZoho.initializeChat(this, "your-zoho-app-key", "your-zoho-access-key")
    }
}
```

#### Using Chat Features

```kotlin
import com.lyracore.zoho.LyraZoho
import com.lyracore.zoho.chat.listeners.ZohoChatListener
import com.lyracore.zoho.chat.models.ChatEvent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Start chat listeners
        LyraZoho.startChatListeners(object : ZohoChatListener {
            override fun onChatOpened(chatEvent: ChatEvent) {
                // Handle chat opened
            }
            
            override fun onChatClosed(chatEvent: ChatEvent) {
                // Handle chat closed
            }
        })
        
        // Open chat
        LyraZoho.openChat(this)
        
        // Configure chat
        LyraZoho.setChatDepartment("Support")
        LyraZoho.setChatLanguage("en")
        LyraZoho.setChatQuestion("I need help with...")
        
        // Set additional information
        val additionalInfo = mapOf(
            "user_id" to "12345",
            "app_version" to "1.0.0"
        )
        LyraZoho.setAdditionalInformation(additionalInfo)
    }
}
```

### iOS (Swift)

#### Basic Setup in AppDelegate

```swift
import LyraZoho

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        // Initialize basic SDK
        let config = LyraConfig(
            apiKey: "your-api-key",
            baseUrl: "https://api.zoho.com",
            timeoutSeconds: 15
        )
        LyraZoho.shared.initialize(config: config)
        
        // Initialize Chat functionality
        LyraZoho.shared.initializeChat(appKey: "your-zoho-app-key", accessKey: "your-zoho-access-key")
        
        return true
    }
}
```

#### Using Chat Features in iOS

```swift
import LyraZoho

class ViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Setup chat listener
        class MyChatListener: ZohoChatListener {
            func onChatOpened(chatEvent: ChatEvent) {
                print("Chat opened: \(chatEvent.chatId ?? "")")
            }
            
            func onChatClosed(chatEvent: ChatEvent) {
                print("Chat closed: \(chatEvent.chatId ?? "")")
            }
        }
        
        let chatListener = MyChatListener()
        LyraZoho.shared.startChatListeners(listener: chatListener)
        
        // Open chat
        LyraZoho.shared.openChat()
        
        // Configure chat
        LyraZoho.shared.setChatDepartment(departmentName: "Support")
        LyraZoho.shared.setChatLanguage(languageCode: "en")
        LyraZoho.shared.setChatQuestion(question: "I need help with...")
        
        // Set additional information
        let additionalInfo = [
            "user_id": "12345",
            "app_version": "1.0.0"
        ]
        LyraZoho.shared.setAdditionalInformation(additionalInfo: additionalInfo)
    }
}
```

## API Reference

### Core SDK Methods

| Method | Description |
|--------|-------------|
| `initialize(config)` | Initialize with full configuration |
| `initializeChat(appKey, accessKey)` | Enable chat functionality |
| `isInitialized()` | Check if basic SDK is initialized |
| `isZohoInitialized()` | Check if chat is initialized |

### Chat Methods

| Method | Description |
|--------|-------------|
| `startChatListeners(listener)` | Start listening to chat events |
| `openChat()` | Open the chat interface |
| `setChatDepartment(name)` | Set chat department |
| `setChatLanguage(code)` | Set chat language |
| `setChatQuestion(question)` | Set initial question |
| `setPageTitle(title)` | Set page title for tracking |
| `setAdditionalInformation(info)` | Set visitor information |
| `getDepartments(callback)` | Get available departments |
| `endChatSession()` | End current chat session |

### Event Types

- `CHAT_OPENED` - Chat session started
- `CHAT_CLOSED` - Chat session ended
- `CHAT_ATTENDED` - Agent joined chat
- `CHAT_MISSED` - Chat was missed
- `CHAT_REOPENED` - Chat session reopened
- `RATING` - Chat rating received
- `FEEDBACK` - Chat feedback received
- `QUEUE_POSITION_CHANGE` - Queue position changed

## Requirements

- **Android**: API level 24+ (Android 7.0)
- **iOS**: iOS 15.0+
- **Zoho SalesIQ Account**: Required for chat functionality

## License

MIT — see [LICENSE](LICENSE).

## Support

For help, contact <support@lyracore.com>.

---

*This SDK now provides comprehensive chat functionality that puts your previous "placeholder" implementation to shame.*2026-01-08: 1.2.9 
