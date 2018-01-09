package mad9132.planets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import mad9132.planets.model.PlanetPOJO;

import static mad9132.planets.MainActivity.BASE_URL;

/**
 * PlanetAdapter.
 *
 */
public class PlanetAdapter extends RecyclerView.Adapter<PlanetAdapter.ViewHolder> {

    private Context               mContext;
    private ArrayList<PlanetPOJO> mPlanets;

    public PlanetAdapter(Context context) {
        this.mContext = context;
        this.mPlanets = new ArrayList<>(8);
    }

    @Override
    public PlanetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View planetView = inflater.inflate(R.layout.list_planet, parent, false);
        ViewHolder viewHolder = new ViewHolder(planetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlanetAdapter.ViewHolder holder, int position) {
        final PlanetPOJO aPlanet = mPlanets.get(position);

        holder.tvName.setText(aPlanet.getName());

        String url = BASE_URL + "/planets/" + aPlanet.getPlanetId() + "/image";
        // force picasso to fetch the planet's image from the internet, and not use the cache
        // handles the case when the image for Pluto is updated
        Picasso.with(mContext)
                .load(url)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.noimagefound)
                .fit()
                .into(holder.imageView);

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "You long clicked " + aPlanet.getName(),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlanets.size();
    }

    public void addPlanet(PlanetPOJO thePlanet) {
        mPlanets.add(thePlanet);
        notifyDataSetChanged();
    }

    public PlanetPOJO getPlanetAt(int index){
        return mPlanets.get(index);
    }

    public void removePlanet(PlanetPOJO thePlanet) {
        mPlanets.remove(thePlanet);
        notifyDataSetChanged();
    }

    public void setPlanets(List<PlanetPOJO> planetsList) {
        mPlanets.clear();
        mPlanets.addAll(planetsList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public ImageView imageView;
        public View mView;

        public ViewHolder(View planetView) {
            super(planetView);

            tvName = (TextView) planetView.findViewById(R.id.planetNameText);
            imageView = (ImageView) planetView.findViewById(R.id.imageView);
            mView = planetView;
        }
    }
}
