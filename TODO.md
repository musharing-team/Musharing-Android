# TODO

## 2019.8.31

### 纯粹前端的问题

1. ✅~记住密码~
2. 注册时自选头像
3. 从注册界面按返回键返回登录界面
4. 主要的活动界面右侧栏(房间)中，成员、播放列表明显的分隔(参考登录后的选择界面)
5. 每次开始播放音量会归零，应与系统音量同步
6. 实体按键调整音量界面上的音量条应该跟着变化
7. 歌曲加载(缓存)过程中应有UI上的表示
8. UI美化
9. 增强App稳定性，减少卡顿、未响应、闪退的发生

### 涉及后端的问题

1. 设置中*账户*功能的实现，实现修改头像、用户名、密码的功能
2. 忘记密码功能
3. 个人收藏(收藏好友、播放列表)

### 重难点

- 播放列表的选择和修改、同步
- ✅~设计中的"Hot Line"功能，即语音消息的发送、接收、播放~
- 进一步降低延迟（目前正常播放下大概恒有2秒的延迟），并解决在播放下一曲时不同设备完成缓存的时间不同，先完成的开始播放造成的播放不同步

## 2019.7.28 (✅ALL DONE)

- utils
    * ✅class Utility
    * ✅class UserUtil
    * ✅class RequestUtil
    * ✅class ParseUtil

- bases
    * ✅class User
    * ✅class Msg
    * ✅class Music
    * ✅class Playlist extends Msg

- requestTasks
    * ✅Interface RequestListener<...>

    * ✅class RequestTask<...> extends AsyncTask<...>
    * ✅class LoginTask extends RequestTask<...>
    * ✅class RegisterTask ~
    * ✅class MemberTask ~
    * ✅class AttendTask ~
    * ✅class SendTask ~
    * ✅class ReceiveTask ~
    * ✅class LeaveTask ~
    * ✅class LogoutTask ~

- recyclerViewAdapters
    * ✅MemberAdapter
    * ✅MsgAdapter
    * ✅MusicAdapter

- asyncPlayer
    * ✅class PlayAsyncer
    * ✅class PlaylistPlayer

- fragments
    * ✅Fragment RoomFragment
    * ✅Fragment PlaylistFragment
    * ✅Fragment ChatFragment
    * ✅Fragment MusicFragment

- activities
    * ✅Activity LoginActivity
    * ✅Activity RegisterActivity

    * ✅Activity RoomPlaylistActivity
    * ✅Activity MusicChatActivity

    * ✅Activity SettingActivity
    * ✅Activity AboutActivity

    * (Temporary abandoned)❌Activity CollectionActivity (Unnecessary, BackEnd supporting needed)