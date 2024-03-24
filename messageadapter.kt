import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.laraib.i210865.R
import com.laraib.i210865.ChatRV

class messageadapter(private val mContext: Context, private val chatList: List<ChatRV>, private val imageurl: String) : RecyclerView.Adapter<messageadapter.ChatViewHolder>() {

    private val fuser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view: View = if (viewType == MSG_TYPE_RIGHT) {
            LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false)
        } else {
            LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false)
        }
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]

        holder.show_message.text = chat.message

        if (imageurl == "default") {
            holder.profile_image.setImageResource(R.drawable.ic_launcher_background)
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val show_message: TextView = itemView.findViewById(R.id.showmessage)
        val profile_image: ImageView = itemView.findViewById(R.id.profileimage)
    }

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].sender == fuser?.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }
}
