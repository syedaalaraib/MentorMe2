import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.laraib.i210865.Item_RV
import com.laraib.i210865.R // Assuming you have an R class in your package

class mentors_adapter(private val mentors_list: MutableList<Item_RV>) : RecyclerView.Adapter<mentors_adapter.mentorsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mentorsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.topmentor_layout, parent, false)
        return mentorsViewHolder(view)
    }

    override fun onBindViewHolder(holder: mentorsViewHolder, position: Int) {
        val mentor = mentors_list[position]
        holder.bind(mentor)
    }

    override fun getItemCount(): Int {
        return mentors_list.size
    }

    inner class mentorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views in mentors_item layout
        private val image1: ImageView = itemView.findViewById(R.id.image1)
        private val image2: ImageView = itemView.findViewById(R.id.image2)
        private val heart: ImageView = itemView.findViewById(R.id.greyheart)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val price: TextView = itemView.findViewById(R.id.charges)
        private val status: TextView = itemView.findViewById(R.id.status)

        fun bind(mentor: Item_RV) {
            // Set values to views
            image1.setImageResource(mentor.image1)
            image2.setImageResource(mentor.image2)
            // Assuming heart is an ImageView, you set its resource accordingly
            heart.setImageResource(mentor.heart)
            name.text = mentor.name
            description.text = mentor.description
            price.text = mentor.price
            status.text = mentor.status
        }
    }
}
