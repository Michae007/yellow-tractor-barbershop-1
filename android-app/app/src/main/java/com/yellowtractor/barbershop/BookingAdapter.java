package com.yellowtractor.barbershop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private MainActivity context;
    private List<Booking> bookingList;

    public BookingAdapter(MainActivity context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booking_item, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        
        // Основная информация
        holder.textClientName.setText(booking.getDisplayInfo());
        holder.textParentName.setText("Родитель: " + booking.getParentName());
        holder.textService.setText("Услуга: " + booking.getService());
        holder.textDateTime.setText("Когда: " + booking.getDisplayDate() + " в " + booking.getTime());
        holder.textPhone.setText("📞 " + booking.getPhone());
        
        // Примечания (если есть)
        if (booking.getNotes() != null && !booking.getNotes().isEmpty()) {
            holder.textNotes.setVisibility(View.VISIBLE);
            holder.textNotes.setText("Примечания: " + booking.getNotes());
        } else {
            holder.textNotes.setVisibility(View.GONE);
        }
        
        // Статус с цветом
        holder.textStatus.setText("Статус: " + booking.getStatus());
        setStatusColor(holder.textStatus, booking.getStatus());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }
    
    private void setStatusColor(TextView statusView, String status) {
        if (status == null) return;
        
        switch (status.toLowerCase()) {
            case "новая":
                statusView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "подтверждена":
                statusView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "выполнена":
                statusView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "отменена":
                statusView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            default:
                statusView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView textClientName, textParentName, textService, textDateTime, textPhone, textNotes, textStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            textClientName = itemView.findViewById(R.id.textClientName);
            textParentName = itemView.findViewById(R.id.textParentName);
            textService = itemView.findViewById(R.id.textService);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            textPhone = itemView.findViewById(R.id.textPhone);
            textNotes = itemView.findViewById(R.id.textNotes);
            textStatus = itemView.findViewById(R.id.textStatus);
        }
    }
}
