#!/usr/bin/env python3
"""
BOOK.md 解析脚本
将易经六十四卦数据手册转换为 JSON 格式
"""

import json
import re
import os

def parse_hexagram(section, index):
    """解析单个卦象"""
    lines = [line.strip() for line in section.split('\n')]

    # 解析标题
    title_pattern = r'##\s*第(\d+)卦\s+(\S+)\(([^)]+)\)\s*(\S*)'
    title_match = re.search(title_pattern, lines[0])
    if not title_match:
        return None

    id = int(title_match.group(1))
    full_name = title_match.group(3)
    unicode_char = title_match.group(4) if len(title_match.groups()) >= 4 else ""

    # 查找特定字段
    def find_field(prefix):
        for line in lines:
            if line.startswith(prefix):
                return line.replace(prefix, '').strip()
        return ""

    # 解析卦象
    trigram_line = find_field("**卦象**:")
    trigram_pattern = r'上([^☰☷☵☲☳☴☶☱]+)[☰☷☵☲☳☴☶☱]下([^☰☷☵☲☳☴☶☱]+)'
    trigram_match = re.search(trigram_pattern, trigram_line)

    if trigram_match:
        upper_trigram = trigram_match.group(1)
        lower_trigram = trigram_match.group(2)
    else:
        # 简单解析
        parts = trigram_line.replace("**卦象**:", "").strip()
        if '上' in parts and '下' in parts:
            upper_trigram = parts.split('下')[0].replace('上', '').strip()
            lower_trigram = re.sub(r'[☰☷☵☲☳☴☶☱].*', '', parts.split('下')[1]).strip()
        else:
            upper_trigram = "乾"
            lower_trigram = "乾"

    # 基本字段
    gua_ci = find_field("**卦辞**:")
    xiang_ci = find_field("**象辞**:")
    tuan_ci = find_field("**彖辞**:")
    application = find_field("**应用**:")

    # 解析爻辞
    yaos = []
    yao_pattern = r'\*\*([初九二三四五六上]+)\*\*:\s*(.+)'
    yao_position = 1

    i = 0
    while i < len(lines) and yao_position <= 6:
        line = lines[i]
        match = re.match(yao_pattern, line)
        if match:
            yao_name = match.group(1)
            # 跳过"用九"和"用六"
            if not yao_name.startswith('用'):
                yao_text = match.group(2)
                xiang_text = ""
                meaning = ""

                if i + 1 < len(lines) and lines[i + 1].startswith('- 象曰:'):
                    xiang_text = lines[i + 1].replace('- 象曰:', '').strip()
                if i + 2 < len(lines) and lines[i + 2].startswith('- 译:'):
                    meaning = lines[i + 2].replace('- 译:', '').strip()

                yaos.append({
                    "position": yao_position,
                    "name": yao_name,
                    "text": yao_text,
                    "xiangText": xiang_text,
                    "meaning": meaning
                })
                yao_position += 1
        i += 1

    # 生成爻组合
    trigram_map = {
        "乾": "111", "坤": "000", "坎": "010", "离": "101",
        "震": "001", "巽": "110", "艮": "100", "兑": "011"
    }
    lines_str = trigram_map.get(lower_trigram, "111") + trigram_map.get(upper_trigram, "111")

    # 判断吉凶
    fortune = "中平"
    if "大吉" in application:
        fortune = "大吉"
    elif "吉" in application:
        fortune = "吉"
    elif "凶" in application:
        fortune = "凶"

    return {
        "id": id,
        "name": full_name,
        "upperTrigram": upper_trigram,
        "lowerTrigram": lower_trigram,
        "lines": lines_str,
        "unicode": unicode_char,
        "guaCi": gua_ci,
        "xiangCi": xiang_ci,
        "tuanCi": tuan_ci,
        "meaning": f"{full_name}的卦义解读",
        "fortune": fortune,
        "career": application,
        "love": application,
        "health": application,
        "wealth": application,
        "yaos": yaos
    }

def main():
    print("正在读取 BOOK.md...")

    # 读取 BOOK.md
    if not os.path.exists('BOOK.md'):
        print("错误：BOOK.md 文件不存在！")
        return

    with open('BOOK.md', 'r', encoding='utf-8') as f:
        content = f.read()

    print("正在解析...")

    # 按 --- 分割
    sections = content.split('\n---\n')
    sections = [s for s in sections if '## 第' in s]

    print(f"找到 {len(sections)} 个卦象")

    hexagrams = []
    for index, section in enumerate(sections):
        try:
            hexagram = parse_hexagram(section.strip(), index)
            if hexagram:
                hexagrams.append(hexagram)
                print(f"  ✓ 第 {hexagram['id']} 卦: {hexagram['name']} ({len(hexagram['yaos'])} 爻)")
        except Exception as e:
            print(f"  ✗ 解析第 {index + 1} 卦时出错: {e}")

    # 生成 JSON
    print("\n正在生成 JSON...")
    output_data = {"hexagrams": hexagrams}

    # 创建输出目录
    output_dir = "app/src/main/res/raw"
    os.makedirs(output_dir, exist_ok=True)

    # 写入文件
    output_file = os.path.join(output_dir, "hexagram_data.json")
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(output_data, f, ensure_ascii=False, indent=2)

    print(f"✓ JSON 文件已生成: {output_file}")
    print(f"✓ 共解析 {len(hexagrams)} 个卦象")

if __name__ == '__main__':
    main()
