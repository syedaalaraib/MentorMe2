import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.laraib.i210865.R
import com.laraib.i210865.User_rv
import com.laraib.i210865.privatemessage

class user_adapter(private val user_list: MutableList<User_rv>, private val mContext: Context) : RecyclerView.Adapter<user_adapter.userViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return userViewHolder(view)
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {
        val mentor = user_list[position]
        holder.bind(mentor)

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, privatemessage::class.java)
            intent.putExtra("userid", mentor.name)// Assuming mentor has a uid property
            intent.putExtra("currentuserid", mentor.uid)


            mContext.startActivity(intent)

            // Handle item click
        }
    }



    override fun getItemCount(): Int {
        return user_list.size
    }

    inner class userViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views in mentors_item layout
        private val heart: ImageView = itemView.findViewById(R.id.image)
        private val name: TextView = itemView.findViewById(R.id.user_name)


        fun bind(mentor: User_rv) {
            // Set values to views

            // Assuming heart is an ImageView, you set its resource accordingly
            heart.setImageResource(mentor.photolink)
            name.text = mentor.name
        }
    }
}
