package ru.farpost.githubsearch.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.farpost.githubsearch.R;
import ru.farpost.githubsearch.ui.util.DefaultRecyclerViewArrayAdapter;

/**
 * Created by eugene on 12/1/16.
 */

public class RepositoriesAdapter extends DefaultRecyclerViewArrayAdapter<Repository,
        RepositoriesAdapter.RepositoryViewHolder> {

    public RepositoriesAdapter(Context context) {
        super(context);
    }

    @Override
    public RepositoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.repository_item, parent, false);
        return new RepositoryViewHolder(view);
    }

    public List<Repository> getRepositories() {
        return mItems;
    }

    @Override
    public void onBindViewHolder(RepositoryViewHolder holder, int position) {
        Repository repository = mItems.get(position);
        holder.nameText.setText(repository.fullName == null ? "" : repository.fullName);
        holder.descriptionText.setText(repository.description == null ? "" : repository.description);
        if (!TextUtils.isEmpty(repository.ownerAvatarUrl)) {
            loadAvatar(repository.ownerAvatarUrl, holder.avatarImage);
        }
    }

    private void loadAvatar(String url, ImageView imageView) {
        Picasso.with(mContext).load(url)
                .resizeDimen(R.dimen.repository_item_avatar_size, R.dimen.repository_item_avatar_size)
                .placeholder(R.drawable.github_avatar_placeholder_60dp)
                .error(R.drawable.github_avatar_placeholder_60dp)
                .into(imageView);
    }

    class RepositoryViewHolder extends DefaultRecyclerViewArrayAdapter.DefaultViewHolder {
        ImageView avatarImage;
        TextView nameText;
        TextView descriptionText;

        RepositoryViewHolder(View view) {
            super(view);
            avatarImage = (ImageView) view.findViewById(R.id.avatarImage);
            nameText = (TextView) view.findViewById(R.id.nameText);
            descriptionText = (TextView) view.findViewById(R.id.descriptionText);
        }
    }
}
