package com.akhdmny.driver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akhdmny.driver.Models.Message;
import com.akhdmny.driver.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> mMessageList;
    private String currentUserId;

    public MessageListAdapter(Context context, List<Message> messageList, String senderId) {
        mContext = context;
        mMessageList = messageList;
        currentUserId = senderId;
        Log.i("Message Size", mMessageList.size() + "");
    }

    @Override
    public int getItemCount() {
        Log.i("Messages Size", mMessageList.size() + "");
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) mMessageList.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                ((SentMessageHolder) holder).setType(message.getType());
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                ((ReceivedMessageHolder) holder).setType(message.getType());
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView contentImage, statusImage;
        Message message;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            contentImage = (ImageView) itemView.findViewById(R.id.content_image);
            statusImage = (ImageView) itemView.findViewById(R.id.status_image);

            contentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (message.getType() == 4) {
                        try {
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(message.getBody());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (message.getType() == 3) {

                    } else if (message.getType() == 2) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?q=" + message.getBody() + "&mode=b"));
                        mContext.startActivity(intent);
                    }
                }
            });

        }

        void bind(Message message) {
            this.message = message;
            if (message.getType() == 1) {
                messageText.setText(message.getBody());
            } else if (message.getType() == 3) {
                Picasso.get().load(message.getBody()).error(R.drawable.place_holder).fit().into(contentImage);
            } else if (message.getType() == 4) {
//                contentImage.setImageDrawable(R.drawable.);
            }
            timeText.setText(message.getTime());

            if (!message.getRead()) {
                statusImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icons8_checkmark2));
            } else {
                statusImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icons8_double_tick2));
            }
        }


        void setType(int type) {
            if (type == 1) {
                contentImage.setVisibility(View.GONE);
                messageText.setVisibility(View.VISIBLE);
            } else {
                messageText.setVisibility(View.GONE);
                contentImage.setVisibility(View.VISIBLE);
                final float scale = mContext.getResources().getDisplayMetrics().density;
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) contentImage.getLayoutParams();
                if (type == 4) {
                    params.width = (int) (100 * scale + 0.5f);
                    params.height = (int) (40 * scale + 0.5f);
                    contentImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.audio_button_image));
                } else if (type == 3) {
                    params.height = (int) (240 * scale + 0.5f);
                    params.width = (int) (240 * scale + 0.5f);
                } else if (type == 2) {
                    params.height = (int) (240 * scale + 0.5f);
                    params.width = (int) (240 * scale + 0.5f);
                    contentImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icons8_marker2));
                }
                contentImage.setLayoutParams(params);
            }
        }

    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage, contentImage;
        Message message;
        MediaPlayer mediaPlayer;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
            contentImage = (ImageView) itemView.findViewById(R.id.content_image);


            contentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (message.getType() == 4) {
                        try {
                            if (mediaPlayer != null) {
                                mediaPlayer.release();
                            }
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(message.getBody());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    } else if (message.getType() == 3) {

                    } else if (message.getType() == 2) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?q=" + message.getBody() + "&mode=b"));
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        void bind(Message message) {
            this.message = message;
            if (message.getType() == 1) {
                messageText.setText(message.getBody());
            } else if (message.getType() == 3) {
                Picasso.get().load(message.getBody()).error(R.drawable.place_holder).fit().into(contentImage);
            }
            timeText.setText(message.getTime());
            nameText.setText(message.getSenderName());
            Picasso.get().load(message.getSenderImage()).error(R.drawable.bubble_circle).fit().into(profileImage);

        }


        void setType(int type) {
            if (type == 1) {
                contentImage.setVisibility(View.GONE);
                messageText.setVisibility(View.VISIBLE);
            } else {
                messageText.setVisibility(View.GONE);
                contentImage.setVisibility(View.VISIBLE);
                final float scale = mContext.getResources().getDisplayMetrics().density;
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) contentImage.getLayoutParams();
                if (type == 4) {
                    params.width = (int) (100 * scale + 0.5f);
                    params.height = (int) (40 * scale + 0.5f);
                    contentImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.audio_button_image));
                } else if (type == 3) {
                    params.height = (int) (240 * scale + 0.5f);
                    params.width = (int) (240 * scale + 0.5f);
                } else if (type == 2) {
                    params.height = (int) (240 * scale + 0.5f);
                    params.width = (int) (240 * scale + 0.5f);
                    contentImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icons8_marker2));
                }
                contentImage.setLayoutParams(params);
            }
        }
    }
}
