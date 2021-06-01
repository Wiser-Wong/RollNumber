# RollNumber
动效滚动的数字

## 截图

![images](https://github.com/Wiser-Wong/RollNumber/blob/master/images/rollNumber.gif)

## 环境配置
    allprojects {
    		repositories {
    			...
    			maven { url 'https://jitpack.io' }
    		}
    	}
    	
    	dependencies {
    	        implementation 'com.github.Wiser-Wong:RollNumber:1.0.2'
    	}

## 使用方法
    rnv_number?.setText(919191898, "你好", "李焕英",true)
    
## 操作指南（RollNumberView）
* rnv_roll_duration：滚动时间
* rnv_roll_random_max_count：最大的随机滚动数
* rnv_start_text：开始文字
* rnv_end_text：结束文字
* rnv_numbers：滚动的数字
* rnv_start_text_size：开始文字大小
* rnv_end_text_size：结束文字大小
* rnv_numbers_size：滚动数字大小
* rnv_start_text_color：开始文字颜色
* rnv_end_text_color：结束文字颜色
* rnv_numbers_color：滚动数字颜色
* rnv_text_stroke_width：文字画笔粗细
* rnv_numbers_padding_left：滚动数字左边距
* rnv_numbers_padding_right：滚动数字右边距
* rnv_auto_animator：是否自动滚动
* rnv_roll_direction：滚动方向 up or down 向上或者向下滚动
* rnv_roll_mode：滚动模式 random or fixed  随机数字或者小于当前数字的数字
* rnv_text_style：画笔风格 fill or stroke or fill_and_stroke
