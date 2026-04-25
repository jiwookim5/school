#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>


typedef struct s_data
{
    char name[20];
    char type[10];
    int min_attack;
    int min_hp;
} t_data;

typedef struct s_my
{
    char name[20];
    char nickname[20];
    char type[10];
    int attack;
    int hp;
    int my_full_hp;
} t_my;

typedef struct s_player_info
{
    t_my my_list[6];
    t_my poketmon;
    int poket_cnt;
    int money;
    int monster_ball;
    int healer;
} t_player_info;
