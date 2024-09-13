set terminal png size 1200,600 enhanced font "Helvetica,12"
set output 'users-helmholtz.png'
set title "Registered RSD users (Helmholtz)"
set boxwidth 0.5
set style fill solid
set yrange [0:]
set grid ytics
set xtics out
set xtics rotate by -45
plot "users-helmholtz.data" using 1:3:xtic(2) with boxes lc "coral" notitle, \
'' u 0:3:3 with labels font "Helvetica,14" offset 0.1,0.5 notitle
